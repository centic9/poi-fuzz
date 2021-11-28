package org.dstadler.poi;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Fuzz {
	public static void fuzzerTestOneInput(byte[] input) {
		try {
			WorkbookFactory.create(new ByteArrayInputStream(input));
		} catch (IOException | EncryptedDocumentException e) {
			// expected here
		}
	}
}
