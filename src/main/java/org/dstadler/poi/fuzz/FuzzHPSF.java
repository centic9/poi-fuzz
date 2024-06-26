package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hpsf.extractor.HPSFPropertiesExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.RecordFormatException;

public class FuzzHPSF {
	public static void fuzzerInitialize() {
		Fuzz.adjustLimits();
	}

	public static void fuzzerTestOneInput(byte[] input) {
		try (POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(input))) {
			String workbookName = HSSFWorkbook.getWorkbookDirEntryName(fs.getRoot());
			fs.createDocumentInputStream(workbookName);

			try (HPSFPropertiesOnlyDocument document = new HPSFPropertiesOnlyDocument(fs)) {
				document.write(NullOutputStream.INSTANCE);
			}

			fs.writeFilesystem(NullOutputStream.INSTANCE);
		} catch (IOException | IllegalArgumentException | IllegalStateException | RecordFormatException |
				 IndexOutOfBoundsException | BufferUnderflowException | NoSuchElementException e) {
			// expected here
		}

		try (HPSFPropertiesExtractor extractor = new HPSFPropertiesExtractor(new POIFSFileSystem(new ByteArrayInputStream(input)))) {
			Fuzz.checkExtractor(extractor);
		} catch (IOException | IllegalArgumentException | IllegalStateException | RecordFormatException |
				 IndexOutOfBoundsException | BufferUnderflowException | NoSuchElementException e) {
			// expected
		}
	}
}
