package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class FuzzXWPF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(input))) {
			doc.write(NullOutputStream.NULL_OUTPUT_STREAM);

			try (XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | /*InvalidFormatException |*/ /*EmptyFileException | NotOfficeXmlFileException |*/ /*POIXMLException |
				XmlValueOutOfRangeException | IllegalArgumentException | RecordFormatException | IllegalStateException |*/
				// TODO: remove these when the code is updated
				RuntimeException e) {
			// expected
		}
	}
}
