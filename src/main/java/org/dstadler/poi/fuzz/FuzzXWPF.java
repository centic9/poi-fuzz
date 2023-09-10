package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class FuzzXWPF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(input))) {
			doc.write(NullOutputStream.INSTANCE);

			// if the document can be parsed, trigger some of the getters
			try {
				doc.getAllEmbeddedParts();
				doc.getAllPictures();
				doc.getAllPackagePictures();
				doc.getPackage();
				doc.getPackagePart();
				doc.getProperties();
				doc.getRelations();
				doc.getCharts();
				doc.getBodyElements();
				doc.getBodyElementsIterator();
				doc.getBodyElementsSpliterator();
			} catch (OpenXML4JException | POIXMLException e) {
				// expected
			}

			try (XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | POIXMLException | RecordFormatException | OpenXML4JRuntimeException |
				 IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
			// expected
		}
	}
}
