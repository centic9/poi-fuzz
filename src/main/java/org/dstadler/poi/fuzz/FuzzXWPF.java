package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class FuzzXWPF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(input))) {
			doc.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XWPFWordExtractor extractor = new XWPFWordExtractor(pkg)) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | InvalidFormatException | /*EmptyFileException | NotOfficeXmlFileException |*/ /*POIXMLException |
				XmlValueOutOfRangeException | IllegalArgumentException | RecordFormatException | IllegalStateException |*/
				// TODO: remove these when the code is updated
				RuntimeException e) {
			// expected
		}
	}
}
