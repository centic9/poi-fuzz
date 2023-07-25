package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.sl.usermodel.SlideShowFactory;
import org.apache.poi.util.RecordFormatException;

public class FuzzHSLF {
	public static void fuzzerTestOneInput(byte[] input) {
		try (HSLFSlideShow slides = new HSLFSlideShow(new ByteArrayInputStream(input))) {
			slides.write(NullOutputStream.INSTANCE);
		} catch (IOException | IllegalArgumentException | RecordFormatException |
				 IllegalStateException | HSLFException | IndexOutOfBoundsException |
				 BufferUnderflowException | POIXMLException | NoSuchElementException e) {
			// expected here
		}

		try (HSLFSlideShowImpl slides = new HSLFSlideShowImpl(new ByteArrayInputStream(input))) {
			slides.write(NullOutputStream.INSTANCE);
		} catch (IOException | IllegalArgumentException | RecordFormatException |
				 IllegalStateException | HSLFException | IndexOutOfBoundsException |
				 BufferUnderflowException | POIXMLException | NoSuchElementException e) {
			// expected here
		}

		try {
			try (SlideShowExtractor<HSLFShape, HSLFTextParagraph> extractor =
					new SlideShowExtractor<>((HSLFSlideShow) SlideShowFactory.create(
							new POIFSFileSystem(new ByteArrayInputStream(input)).getRoot()))) {
				Fuzz.checkExtractor(extractor);
			}
		} catch (IOException | IllegalArgumentException | RecordFormatException |
				 IllegalStateException | HSLFException | IndexOutOfBoundsException |
				 BufferUnderflowException | POIXMLException | NoSuchElementException e) {
			// expected here
		}
	}
}
