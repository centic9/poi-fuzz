package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.datatypes.DirectoryChunk;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.apache.poi.util.RecordFormatException;

public class FuzzHSMF {
	public static void fuzzerInitialize() {
		Fuzz.adjustLimits();
	}

	public static void fuzzerTestOneInput(byte[] input) {
		try (MAPIMessage mapi = new MAPIMessage(new ByteArrayInputStream(input))) {
			mapi.getAttachmentFiles();
			mapi.getDisplayBCC();
			mapi.getMessageDate();

			AttachmentChunks[] attachments = mapi.getAttachmentFiles();
			for (AttachmentChunks attachment : attachments) {
				DirectoryChunk chunkDirectory = attachment.getAttachmentDirectory();
				if (chunkDirectory != null) {
					MAPIMessage attachmentMSG = chunkDirectory.getAsEmbeddedMessage();
					attachmentMSG.getTextBody();
				}
			}
		} catch (IOException | ChunkNotFoundException | IllegalArgumentException |
				 RecordFormatException | IndexOutOfBoundsException | NoSuchElementException |
				 BufferUnderflowException | IllegalStateException e) {
			// expected here
		}
	}
}
