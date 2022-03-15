package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.DocumentFormatException;
import org.apache.poi.util.RecordFormatException;

public class FuzzHWPF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (HWPFDocument doc = new HWPFDocument(new ByteArrayInputStream(input))) {
			doc.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EmptyFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try {
			try (WordExtractor extractor = new WordExtractor(
							new POIFSFileSystem(new ByteArrayInputStream(input)).getRoot())) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | /*EncryptedPowerPointFileException |*/ /*OldPowerPointFormatException |*/ IndexOutOfBoundsException |
				RecordFormatException | IllegalArgumentException |
				IllegalStateException | BufferUnderflowException | NoSuchElementException | UnsupportedOperationException |
				NegativeArraySizeException | ClassCastException | DocumentFormatException |
				// TODO: remove these when the code is updated
				AssertionError e) {
			// expected here
		}
	}
}
