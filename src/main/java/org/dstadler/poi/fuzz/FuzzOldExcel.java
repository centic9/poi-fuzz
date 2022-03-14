package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.hssf.extractor.OldExcelExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class FuzzOldExcel {
	public static void fuzzerTestOneInput(byte[] input) {
		try {
			try (OldExcelExtractor extractor = new OldExcelExtractor(
							new POIFSFileSystem(new ByteArrayInputStream(input)).getRoot())) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | /*EncryptedPowerPointFileException |*/ /*OldPowerPointFormatException |*/ /*IndexOutOfBoundsException |
				RecordFormatException | IllegalArgumentException |
				IllegalStateException | BufferUnderflowException | NoSuchElementException |
				RecordInputStream.LeftoverDataException |*/
				// TODO: remove these when the code is updated
				AssertionError | RuntimeException e) {
			// expected here
		}
	}
}
