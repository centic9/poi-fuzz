package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.poi.hssf.extractor.OldExcelExtractor;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.RecordFormatException;

public class FuzzOldExcel {
	public static void fuzzerInitialize() {
		Fuzz.adjustLimits();
	}

	public static void fuzzerTestOneInput(byte[] input) {
		try {
			try (OldExcelExtractor extractor = new OldExcelExtractor(
							new POIFSFileSystem(new ByteArrayInputStream(input)).getRoot())) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | IllegalArgumentException | RecordFormatException | IndexOutOfBoundsException |
				 RecordInputStream.LeftoverDataException | IllegalStateException | BufferUnderflowException |
				 NoSuchElementException e) {
			// expected here
		}
	}
}
