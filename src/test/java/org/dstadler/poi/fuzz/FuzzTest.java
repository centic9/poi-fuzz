package org.dstadler.poi.fuzz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
		// trigger loading the class and thus call the static code
		Fuzz.fuzzerTestOneInput(new byte[] {});

		// need to create the logger after initializing
		Logger LOG = LogManager.getLogger(FuzzTest.class);

		// should not be logged
		LOG.atError().log("Test log output which should not be visible -----------------------");

	}
}