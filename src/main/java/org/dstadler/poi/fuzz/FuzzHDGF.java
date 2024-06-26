package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hdgf.HDGFDiagram;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.RecordFormatException;

public class FuzzHDGF {
	public static void fuzzerInitialize() {
		Fuzz.adjustLimits();
	}

	public static void fuzzerTestOneInput(byte[] input) {
		try (HDGFDiagram visio = new HDGFDiagram(new POIFSFileSystem(new ByteArrayInputStream(input)))) {
			visio.write(NullOutputStream.INSTANCE);

			try (VisioTextExtractor extractor = new VisioTextExtractor(visio)) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | IllegalArgumentException | IllegalStateException | RecordFormatException |
				 IndexOutOfBoundsException | BufferUnderflowException | ArithmeticException |
				 NoSuchElementException e) {
			// expected here
		}
	}
}
