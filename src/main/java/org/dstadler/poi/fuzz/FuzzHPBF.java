package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hpbf.HPBFDocument;
import org.apache.poi.hpbf.extractor.PublisherTextExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class FuzzHPBF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (HPBFDocument wb = new HPBFDocument(new ByteArrayInputStream(input))) {
			wb.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*OfficeXmlFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try {
			try (PublisherTextExtractor extractor = new PublisherTextExtractor (
							new POIFSFileSystem(new ByteArrayInputStream(input)).getRoot())) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | /*EncryptedPowerPointFileException |*/ /*OldPowerPointFormatException |*/ /*IndexOutOfBoundsException |
				RecordFormatException | IllegalArgumentException |
				IllegalStateException | BufferUnderflowException | NoSuchElementException | ClassCastException |*/
				// TODO: remove these when the code is updated
				AssertionError | RuntimeException e) {
			// expected here
		}
	}
}
