package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xslf.extractor.XSLFExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.xmlbeans.XmlException;

public class FuzzXSLF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XMLSlideShow slides = new XMLSlideShow(new ByteArrayInputStream(input))) {
			slides.write(NullOutputStream.INSTANCE);
		} catch (IOException | POIXMLException | RecordFormatException | OpenXML4JRuntimeException |
				 IndexOutOfBoundsException | IllegalArgumentException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XSLFSlideShow slides = new XSLFSlideShow(pkg)) {
				slides.write(NullOutputStream.INSTANCE);
			}
		} catch (IOException | OpenXML4JException | XmlException | IllegalArgumentException | POIXMLException |
				 RecordFormatException | IllegalStateException | OpenXML4JRuntimeException | IndexOutOfBoundsException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XSLFExtractor extractor = new XSLFExtractor(new XMLSlideShow(pkg))) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | InvalidFormatException | POIXMLException | IllegalArgumentException |
				RecordFormatException | IllegalStateException | IndexOutOfBoundsException e) {
			// expected
		}
	}
}
