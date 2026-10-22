package org.dstadler.poi.fuzz;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.xssf.binary.XSSFBParseException;
import org.apache.poi.xssf.extractor.XSSFBEventBasedExcelExtractor;
import org.apache.xmlbeans.XmlException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class POIXSSFBFuzzer {
	public static void fuzzerInitialize() {
		POIFuzzer.adjustLimits();
	}

	public static void fuzzerTestOneInput(byte[] input) {
		if (input.length == 0) {
			return;
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input), true)) {
			XSSFBEventBasedExcelExtractor ex = new XSSFBEventBasedExcelExtractor(pkg);
			ex.getText();

			/* Not supported:
			ex.setFormulasNotResults(true);
			ex.getText();*/

			ex.setHandleHyperlinksInCells(true);
			ex.getText();
		} catch (IOException | XmlException | OpenXML4JException | POIXMLException |
                 RecordFormatException | IndexOutOfBoundsException | IllegalArgumentException |
				 IllegalStateException | ArithmeticException | XSSFBParseException ex) {
			// expected
		} catch (NullPointerException e) {
			// can be removed when commons-compress is > 1.28.0 and changes from COMPRESS-598 are applied
			if (!ExceptionUtils.getStackTrace(e).contains("org.apache.commons.compress.archivers.zip.ZipArchiveInputStream.read")) {
				throw e;
			}
        }
	}
}
