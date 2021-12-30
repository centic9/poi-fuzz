package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
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
		} catch (IOException | /*EncryptedDocumentException | EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (POITextExtractor extractor = ExtractorFactory.createExtractor(new ByteArrayInputStream(input))) {
			extractor.getDocument();
			extractor.getFilesystem();
			extractor.getMetadataTextExtractor();
			extractor.getText();
		} catch (IOException | /*EmptyFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(input))) {
			String workbookName = HSSFWorkbook.getWorkbookDirEntryName(fs.getRoot());
			fs.createDocumentInputStream(workbookName);

			try (HPSFPropertiesOnlyDocument ignored = new HPSFPropertiesOnlyDocument(fs)) {
			}
		} catch (IOException | /*OldExcelFormatException | OfficeXmlFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HSSFWorkbook ignored = new HSSFWorkbook(new ByteArrayInputStream(input))) {
		} catch (IOException | /*OfficeXmlFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HSLFSlideShow ignored = new HSLFSlideShow(new ByteArrayInputStream(input))) {
		} catch (IOException | /*OfficeXmlFileException | EncryptedPowerPointFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HSLFSlideShowImpl ignored = new HSLFSlideShowImpl(new ByteArrayInputStream(input))) {
		} catch (IOException | /*OfficeXmlFileException | EncryptedPowerPointFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (HWPFDocument ignored = new HWPFDocument(new ByteArrayInputStream(input))) {
		} catch (IOException | /*EmptyFileException | EncryptedDocumentException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(input))) {
			try (SXSSFWorkbook ignored = new SXSSFWorkbook(wb)) {
			}
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (XWPFDocument ignored = new XWPFDocument(new ByteArrayInputStream(input))) {
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (XMLSlideShow ignored = new XMLSlideShow(new ByteArrayInputStream(input))) {
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XSLFSlideShow ignored = new XSLFSlideShow(pkg)) {
			}
		} catch (IOException | OpenXML4JException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException |
				// TODO: wrap exceptions from XmlBeans
				XmlException e) {
			// expected here
		}

		try (XmlVisioDocument ignored = new XmlVisioDocument(new ByteArrayInputStream(input))) {
		} catch (IOException | /*EmptyFileException | NotOfficeXmlFileException |*/
				AssertionError | RuntimeException e) {
			// expected here
		}
	}
}
