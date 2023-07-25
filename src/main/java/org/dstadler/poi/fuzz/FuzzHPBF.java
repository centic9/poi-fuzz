package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hpbf.HPBFDocument;
import org.apache.poi.hpbf.extractor.PublisherTextExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.RecordFormatException;

public class FuzzHPBF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (HPBFDocument wb = new HPBFDocument(new ByteArrayInputStream(input))) {
			wb.write(NullOutputStream.INSTANCE);
		} catch (IOException | IllegalArgumentException | RecordFormatException | IndexOutOfBoundsException |
				 BufferUnderflowException | IllegalStateException | NoSuchElementException e) {
			// expected here
		}

		try {
			try (PublisherTextExtractor extractor = new PublisherTextExtractor (
							new POIFSFileSystem(new ByteArrayInputStream(input)).getRoot())) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | IllegalArgumentException | RecordFormatException | IndexOutOfBoundsException |
				BufferUnderflowException | IllegalStateException | NoSuchElementException e) {
			// expected here
		}
	}
}
