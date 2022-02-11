package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hmef.HMEFMessage;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

/**
 * This class provides a simple target for fuzzing Apache POI with Jazzer.
 *
 * It uses the byte-array to call various method which parse the varoius
 * supported file-formats.
 *
 * It catches all exceptions that are currently expected.
 *
 * Some are marked as to-do where Apache POI should actually do
 * exception handling differently, e.g. wrapping internal exceptions
 * or providing more explicit exceptions instead of general RuntimeExceptions
 */
public class Fuzz {
	@SuppressWarnings("EmptyTryBlock")
	public static void fuzzerTestOneInput(byte[] input) {
		// try to invoke various methods which parse documents/workbooks/slide-shows/...

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

		try (POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(input))) {
			String workbookName = HSSFWorkbook.getWorkbookDirEntryName(fs.getRoot());
			fs.createDocumentInputStream(workbookName);

			try (HPSFPropertiesOnlyDocument ignored = new HPSFPropertiesOnlyDocument(fs)) {
			}

			fs.writeFilesystem(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*OldExcelFormatException | OfficeXmlFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HSSFWorkbook wb = new HSSFWorkbook(new ByteArrayInputStream(input))) {
			wb.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*OfficeXmlFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HSLFSlideShow slides = new HSLFSlideShow(new ByteArrayInputStream(input))) {
			slides.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*OfficeXmlFileException | EncryptedPowerPointFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HSLFSlideShowImpl slides = new HSLFSlideShowImpl(new ByteArrayInputStream(input))) {
			slides.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*OfficeXmlFileException | EncryptedPowerPointFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HWPFDocument doc = new HWPFDocument(new ByteArrayInputStream(input))) {
			doc.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EmptyFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(input))) {
			try (SXSSFWorkbook swb = new SXSSFWorkbook(wb)) {
				swb.write(NullOutputStream.NULL_OUTPUT_STREAM);
			}
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(input))) {
			doc.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

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

		try (XmlVisioDocument visio = new XmlVisioDocument(new ByteArrayInputStream(input))) {
			visio.write(NullOutputStream.NULL_OUTPUT_STREAM);
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try {
			HMEFMessage msg = new HMEFMessage(new ByteArrayInputStream(input));
			//noinspection ResultOfMethodCallIgnored
			msg.getAttachments();
			msg.getBody();
			//noinspection ResultOfMethodCallIgnored
			msg.getMessageAttributes();
			msg.getSubject();
			//noinspection ResultOfMethodCallIgnored
			msg.getMessageMAPIAttributes();
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}
	}

	private static void checkExtractor(byte[] input) {
		try (POITextExtractor extractor = ExtractorFactory.createExtractor(new ByteArrayInputStream(input))) {
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
		} catch (IOException | /*EmptyFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}
	}
}
