package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xslf.extractor.XSLFExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.xmlbeans.XmlException;

public class FuzzXSLF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XMLSlideShow slides = new XMLSlideShow(new ByteArrayInputStream(input))) {
			slides.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XSLFSlideShow slides = new XSLFSlideShow(pkg)) {
				slides.write(NullOutputStream.NULL_OUTPUT_STREAM);
			}
		} catch (IOException | OpenXML4JException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException |
				// TODO: wrap exceptions from XmlBeans
				XmlException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XSLFExtractor extractor = new XSLFExtractor(new XMLSlideShow(pkg))) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | InvalidFormatException | /*EmptyFileException | NotOfficeXmlFileException |*/ POIXMLException |
				/*XmlValueOutOfRangeException |*/ IllegalArgumentException | RecordFormatException | IllegalStateException e) {
			// expected
		}
	}
}
