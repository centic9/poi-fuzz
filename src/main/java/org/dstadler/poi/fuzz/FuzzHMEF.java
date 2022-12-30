package org.dstadler.poi.fuzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.hmef.HMEFMessage;
import org.apache.poi.util.RecordFormatException;

public class FuzzHMEF {
	public static void fuzzerTestOneInput(byte[] input) {
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
		} catch (IOException | IllegalArgumentException | IllegalStateException | RecordFormatException |
				ArrayIndexOutOfBoundsException e) {
			// expected here
		}
	}
}
