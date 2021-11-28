package org.dstadler.poi.fuzz;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class FuzzTest {
	@Test
	public void test() {
		Fuzz.fuzzerTestOneInput(new byte[] {});
		Fuzz.fuzzerTestOneInput(new byte[] {1});
		Fuzz.fuzzerTestOneInput(new byte[] {'P', 'K'});
	}

	@Test
	public void testLog() {
		// should not be logged
		Logger LOG = LogManager.getLogger(FuzzTest.class);
		LOG.atError().log("Test log output which should not be visible -----------------------");
	}

	@Disabled("Local test for verifying a slow run")
	@Test
	public void testSlowUnit() throws IOException {
		Fuzz.fuzzerTestOneInput(FileUtils.readFileToByteArray(new File("slow-unit-0a0b0ce97bb332cd9f8fde03e03840768a81d29d")));
	}
}