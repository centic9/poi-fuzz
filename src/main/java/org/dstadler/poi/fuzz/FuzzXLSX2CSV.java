package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.NullPrintStream;
import org.apache.poi.examples.xssf.eventusermodel.XLSX2CSV;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.RecordFormatException;
import org.xml.sax.SAXException;

public class FuzzXLSX2CSV {
	public static void fuzzerTestOneInput(byte[] input) {
		try (InputStream in = new ByteArrayInputStream(input)) {
			OPCPackage p = OPCPackage.open(in);
			XLSX2CSV xlsx2csv = new XLSX2CSV(p, NullPrintStream.INSTANCE, 5);
			xlsx2csv.process();
		} catch (UnsatisfiedLinkError e) {
			// only allow one missing library related to Font/Color-handling
			// we cannot install additional libraries in oss-fuzz images currently
			// see https://github.com/google/oss-fuzz/issues/7380
			if (!e.getMessage().contains("libawt_xawt.so")) {
				throw e;
			}
		} catch (IOException | OpenXML4JException | SAXException |
				 POIXMLException | RecordFormatException |
				IllegalStateException | IllegalArgumentException |
				IndexOutOfBoundsException e) {
			// expected here
		}
	}
}