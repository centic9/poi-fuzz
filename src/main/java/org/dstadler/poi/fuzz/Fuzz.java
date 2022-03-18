package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * This class provides a simple target for fuzzing Apache POI with Jazzer.
 *
 * It uses the byte-array to call various method which parse the various
 * supported file-formats.
 *
 * It catches all exceptions that are currently expected.
 *
 * Some are marked as to-do where Apache POI should actually do
 * exception handling differently, e.g. wrapping internal exceptions
 * or providing more explicit exceptions instead of general RuntimeExceptions
 */
public class Fuzz {
	public static void fuzzerTestOneInput(byte[] input) {
		// try to invoke various methods which parse documents/workbooks/slide-shows/...

		fuzzAny(input);

		FuzzHDGF.fuzzerTestOneInput(input);

		FuzzHPSF.fuzzerTestOneInput(input);

		FuzzHSSF.fuzzerTestOneInput(input);

		FuzzHSLF.fuzzerTestOneInput(input);

		FuzzHWPF.fuzzerTestOneInput(input);

		FuzzXSSF.fuzzerTestOneInput(input);

		FuzzXWPF.fuzzerTestOneInput(input);

		FuzzXSLF.fuzzerTestOneInput(input);

		FuzzVisio.fuzzerTestOneInput(input);

		FuzzHMEF.fuzzerTestOneInput(input);

		FuzzHPBF.fuzzerTestOneInput(input);
	}

	public static void fuzzAny(byte[] input) {
		try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(input))) {
			for (Sheet sheet : wb) {
				for (Row row : sheet) {
					for (Cell cell : row) {
						cell.getAddress();
						cell.getCellType();
					}
				}
			}

			wb.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EncryptedDocumentException | EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		ExtractorFactory.setThreadPrefersEventExtractors(true);
		checkExtractor(input);
		ExtractorFactory.setAllThreadsPreferEventExtractors(false);
		checkExtractor(input);
	}

	public static void checkExtractor(byte[] input) {
		try (POITextExtractor extractor = ExtractorFactory.createExtractor(new ByteArrayInputStream(input))) {
			checkExtractor(extractor);
		} catch (IOException | /*EmptyFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}
	}

	public static void checkExtractor(POITextExtractor extractor) throws IOException {
		extractor.getDocument();
		extractor.getFilesystem();
		extractor.getMetadataTextExtractor();
		extractor.getText();

		if (extractor instanceof POIOLE2TextExtractor) {
			POIOLE2TextExtractor ole2Extractor = (POIOLE2TextExtractor) extractor;
			ole2Extractor.getRoot();
			ole2Extractor.getSummaryInformation();
			ole2Extractor.getDocSummaryInformation();

			POITextExtractor[] embedded = ExtractorFactory.getEmbeddedDocsTextExtractors(ole2Extractor);
			for (POITextExtractor poiTextExtractor : embedded) {
				poiTextExtractor.getText();
				poiTextExtractor.getDocument();
				poiTextExtractor.getFilesystem();
				POITextExtractor metaData = poiTextExtractor.getMetadataTextExtractor();
				metaData.getFilesystem();
				metaData.getText();
			}
		} else if (extractor instanceof POIXMLTextExtractor) {
			POIXMLTextExtractor xmlExtractor = (POIXMLTextExtractor) extractor;
			xmlExtractor.getCoreProperties();
			xmlExtractor.getCustomProperties();
			xmlExtractor.getExtendedProperties();
			POIXMLPropertiesTextExtractor metaData = xmlExtractor.getMetadataTextExtractor();
			metaData.getFilesystem();
			metaData.getText();

			xmlExtractor.getPackage();
		}
	}
}
