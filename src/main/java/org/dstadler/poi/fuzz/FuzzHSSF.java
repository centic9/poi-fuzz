package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.RecordFormatException;

public class FuzzHSSF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (HSSFWorkbook wb = new HSSFWorkbook(new ByteArrayInputStream(input))) {
			wb.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | IllegalArgumentException | RecordFormatException | IllegalStateException |
				 IndexOutOfBoundsException | RecordInputStream.LeftoverDataException |
				 BufferUnderflowException | UnsupportedOperationException | NoSuchElementException |
				NegativeArraySizeException e) {
			// expected here
		}

		try {
			try (ExcelExtractor extractor = new ExcelExtractor(
							new POIFSFileSystem(new ByteArrayInputStream(input)).getRoot())) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | IllegalArgumentException | RecordFormatException | IllegalStateException |
				 IndexOutOfBoundsException | RecordInputStream.LeftoverDataException |
				 BufferUnderflowException | UnsupportedOperationException | NoSuchElementException |
				 NegativeArraySizeException | HSLFException e) {
			// expected here
		}
	}
}
