package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.EmptyFileException;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;

public class FuzzVisio {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XmlVisioDocument visio = new XmlVisioDocument(new ByteArrayInputStream(input))) {
			visio.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | POIXMLException | EmptyFileException | UnsupportedFileFormatException |
				 BufferUnderflowException | RecordFormatException | OpenXML4JRuntimeException e) {
			// expected here
		}

		try (VisioTextExtractor extractor = new VisioTextExtractor(new ByteArrayInputStream(input))) {
			Fuzz.checkExtractor(extractor);
		} catch (IOException | POIXMLException | IllegalArgumentException | BufferUnderflowException |
				 RecordFormatException | IndexOutOfBoundsException | ArithmeticException | IllegalStateException e) {
			// expected
		}
	}
}
