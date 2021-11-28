package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.EmptyFileException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

public class Fuzz {
	@SuppressWarnings("EmptyTryBlock")
	public static void fuzzerTestOneInput(byte[] input) {
		// try to invoke various methods which parse documents/workbooks/slide-shows/...

		try (Workbook ignored = WorkbookFactory.create(new ByteArrayInputStream(input))) {
		} catch (IOException | EncryptedDocumentException | EmptyFileException | NotOfficeXmlFileException e) {
			// expected here
		}

		try (POITextExtractor ignored = ExtractorFactory.createExtractor(new ByteArrayInputStream(input))) {
		} catch (IOException | EmptyFileException e) {
			// expected here
		}

		try (POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(input))) {
			String workbookName = HSSFWorkbook.getWorkbookDirEntryName(fs.getRoot());
			fs.createDocumentInputStream(workbookName);

			try (HPSFPropertiesOnlyDocument ignored = new HPSFPropertiesOnlyDocument(fs)) {
			}
		} catch (IOException | OldExcelFormatException e) {
			// expected here
		}

		try (HSSFWorkbook ignored = new HSSFWorkbook(new ByteArrayInputStream(input))) {
		} catch (IOException e) {
			// expected here
		}

		try (HSLFSlideShow ignored = new HSLFSlideShow(new ByteArrayInputStream(input))) {
		} catch (IOException e) {
			// expected here
		}

		try (HSLFSlideShowImpl ignored = new HSLFSlideShowImpl(new ByteArrayInputStream(input))) {
		} catch (IOException e) {
			// expected here
		}

		try (HWPFDocument ignored = new HWPFDocument(new ByteArrayInputStream(input))) {
		} catch (IOException | /*EmptyFileException | */ IllegalArgumentException e) {
			// expected here
		}

		try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(input))) {
			try (SXSSFWorkbook ignored = new SXSSFWorkbook(wb)) {
			}
		} catch (IOException | EmptyFileException | NotOfficeXmlFileException e) {
			// expected here
		}

		try (XWPFDocument ignored = new XWPFDocument(new ByteArrayInputStream(input))) {
		} catch (IOException | EmptyFileException | NotOfficeXmlFileException e) {
			// expected here
		}

		try (XMLSlideShow ignored = new XMLSlideShow(new ByteArrayInputStream(input))) {
		} catch (IOException | EmptyFileException | NotOfficeXmlFileException e) {
			// expected here
		}

		try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(input))) {
			try (XSLFSlideShow ignored = new XSLFSlideShow(pkg)) {
			}
		} catch (IOException | OpenXML4JException | XmlException | EmptyFileException | NotOfficeXmlFileException e) {
			// expected here
		}

		try (XmlVisioDocument ignored = new XmlVisioDocument(new ByteArrayInputStream(input))) {
		} catch (IOException | EmptyFileException | NotOfficeXmlFileException e) {
			// expected here
		}
	}
}
