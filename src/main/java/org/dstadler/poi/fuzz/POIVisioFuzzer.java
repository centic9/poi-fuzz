package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;

public class POIVisioFuzzer {
	public static void fuzzerInitialize() {
		POIFuzzer.adjustLimits();
	}

	public static void fuzzerTestOneInput(byte[] input) {
		try (XmlVisioDocument visio = new XmlVisioDocument(new ByteArrayInputStream(input))) {
			visio.write(NullOutputStream.INSTANCE);
		} catch (IOException | POIXMLException |
				 BufferUnderflowException | RecordFormatException | OpenXML4JRuntimeException |
				 IllegalArgumentException | IndexOutOfBoundsException | IllegalStateException e) {
			// expected here
		} catch (NullPointerException e) {
			// can be removed when commons-compress is > 1.28.0 and changes from COMPRESS-598 are applied
			if (!ExceptionUtils.getStackTrace(e).contains("org.apache.commons.compress.archivers.zip.ZipArchiveInputStream.read")) {
				throw e;
			}
		}

		try (VisioTextExtractor extractor = new VisioTextExtractor(new ByteArrayInputStream(input))) {
			POIFuzzer.checkExtractor(extractor);
		} catch (IOException | POIXMLException | IllegalArgumentException | BufferUnderflowException |
				 RecordFormatException | IndexOutOfBoundsException | ArithmeticException | IllegalStateException |
				 NoSuchElementException e) {
			// expected
		}
	}
}
