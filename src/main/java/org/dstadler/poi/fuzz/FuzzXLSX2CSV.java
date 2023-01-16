package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.NullPrintStream;
import org.apache.poi.EmptyFileException;
import org.apache.poi.examples.xssf.eventusermodel.XLSX2CSV;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.xml.sax.SAXException;

public class FuzzXLSX2CSV {
	public static void fuzzerTestOneInput(byte[] input) {
			InputStream in = new ByteArrayInputStream(input);
		try {
			OPCPackage p = OPCPackage.open(in);
			XLSX2CSV xlsx2csv = new XLSX2CSV(p, new NullPrintStream(), 5);
			xlsx2csv.process();
		} catch (IOException | OpenXML4JException | SAXException | EmptyFileException |
				 NotOfficeXmlFileException e) {
			// expected here
		}
	}
}