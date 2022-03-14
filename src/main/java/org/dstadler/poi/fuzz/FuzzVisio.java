package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;

public class FuzzVisio {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XmlVisioDocument visio = new XmlVisioDocument(new ByteArrayInputStream(input))) {
			visio.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (VisioTextExtractor extractor = new VisioTextExtractor(new ByteArrayInputStream(input))) {
			Fuzz.checkExtractor(extractor);
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/ POIXMLException |
				/*XmlValueOutOfRangeException |*/ IllegalArgumentException | RecordFormatException | IllegalStateException |
				IndexOutOfBoundsException | BufferUnderflowException | NoSuchElementException e) {
			// expected
		}
	}
}
