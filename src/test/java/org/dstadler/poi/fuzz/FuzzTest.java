package org.dstadler.poi.fuzz;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xdgf.usermodel.section.GeometrySection;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;

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

	@Disabled("Fails in Apache POI 5.2.0 and current snapshots if poi-ooxml-lite is used")
	@Test
	public void testSnapshot() {
		SectionType sectionType = mock(SectionType.class);
		RowType rowType = mock(RowType.class);

		when(sectionType.getCellArray()).thenReturn(new CellType[0]);
		when(sectionType.getRowArray()).thenReturn(new RowType[] {
				rowType
		});
		when(rowType.getIX()).thenReturn(0L);
		when(rowType.getT()).thenReturn("ArcTo");
		when(rowType.getCellArray()).thenReturn(new CellType[0]);

		GeometrySection section = new GeometrySection(sectionType, null);
		assertNotNull(section);
	}

	@Disabled("Local test for verifying reproducing test-cases")
	@Test
	public void testOOM() throws IOException {
		File file = new File("/Crash_f6febecb9c9aede225616731e0d0174d1b91fe52.java");
		byte[] input = FileUtils.readFileToByteArray(file);
		System.out.println("Processing " + file.getName() + " with " + input.length + " bytes");
		FuzzHDGF.fuzzerTestOneInput(input);
	}

	@Disabled("Local test for verifying reproducing test-cases")
	@Test
	public void testOOMMultiple() throws IOException {
		int i = 1;
		File corpusDir = new File("corpusXSSF");
		File[] files = corpusDir.listFiles();
		assertNotNull(files);
		for (File file : files) {
			byte[] input = FileUtils.readFileToByteArray(file);
			System.out.println(i + ": Processing " + file.getName() + " with " + input.length + " bytes");
			FuzzHDGF.fuzzerTestOneInput(input);
			i++;
		}
	}

	@Disabled("Local test for verifying a slow run")
	@Test
	public void testSlowUnit() throws IOException {
		for (int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();

			FuzzXWPF.fuzzerTestOneInput(FileUtils.readFileToByteArray(new File("timeout-54064ba65ddc1976d2d26f5c21b369d845120355")));

			System.out.println("Took " + (System.currentTimeMillis() - start));
		}
	}
}