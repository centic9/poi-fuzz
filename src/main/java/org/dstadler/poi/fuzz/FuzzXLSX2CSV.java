package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.NullPrintStream;
import org.apache.poi.examples.xssf.eventusermodel.XLSX2CSV;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.RecordFormatException;
import org.xml.sax.SAXException;

public class FuzzXLSX2CSV {
	public static void fuzzerInitialize() {
		Fuzz.adjustLimits();
	}

	public static void fuzzerTestOneInput(byte[] input) {
		try (InputStream in = new ByteArrayInputStream(input)) {
			OPCPackage p = OPCPackage.open(in);
			XLSX2CSV xlsx2csv = new XLSX2CSV(p, NullPrintStream.INSTANCE, 5);
			xlsx2csv.process();
		} catch (IOException | OpenXML4JException | SAXException |
				 POIXMLException | RecordFormatException |
				 IllegalStateException | IllegalArgumentException |
				 IndexOutOfBoundsException | OpenXML4JRuntimeException e) {
			// expected here
		}
	}
}