package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hdgf.HDGFDiagram;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class FuzzHDGF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (HDGFDiagram visio = new HDGFDiagram(new POIFSFileSystem(new ByteArrayInputStream(input)))) {
			visio.write(NullOutputStream.NULL_OUTPUT_STREAM);

			try (VisioTextExtractor extractor = new VisioTextExtractor(visio)) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}
	}
}
