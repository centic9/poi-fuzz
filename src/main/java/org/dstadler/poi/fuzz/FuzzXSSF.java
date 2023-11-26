package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;

public class FuzzXSSF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(input))) {
			try (SXSSFWorkbook swb = new SXSSFWorkbook(wb)) {
				swb.write(NullOutputStream.INSTANCE);
			}
		} catch (IOException | POIXMLException | RecordFormatException | IllegalStateException |
				 OpenXML4JRuntimeException | IllegalArgumentException | IndexOutOfBoundsException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XSSFEventBasedExcelExtractor extractor = new XSSFEventBasedExcelExtractor(pkg)) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | XmlException | OpenXML4JException | POIXMLException | RecordFormatException |
				IllegalStateException | IllegalArgumentException | IndexOutOfBoundsException e) {
			// expected
		}
	}
}
