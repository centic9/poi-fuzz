package org.dstadler.poi.fuzz;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A simple test which runs all files from the corpus to be
 * able to see where we are missing coverage, i.e. which types
 * of corpus files are missing.
 *
 * Disabled as it can run for a long time.
 *
 * Note: There is now a script "coverageReport.sh" which produces a coverage
 * report.
 */
@Execution(ExecutionMode.CONCURRENT)
public class CorpusCoverageTest {

	private static final Set<String> EXCLUDED = Set.of();
	public static final IOFileFilter EXCLUDE_FILE_FILTER = new NotFileFilter(
			new OrFileFilter(
					new NameFileFilter(".svn"),
					new NameFileFilter(".git")
			)
	);

	@Disabled("Can run a long time when there is a large corpus used for fuzzing")
	@ParameterizedTest
	@MethodSource("files")
	void testCorpusFile(File file) throws IOException {
		System.err.println("Running file " + file + " in thread " + Thread.currentThread().getName());
		Fuzz.fuzzerTestOneInput(FileUtils.readFileToByteArray(file));
	}

	@Disabled("Can run a long time when there is a large corpus used for fuzzing")
	@ParameterizedTest
	@MethodSource("files")
	void testCorpusFileOne(File file) throws IOException {
		System.err.println("Running file " + file + " in thread " + Thread.currentThread().getName());
		FuzzHDGF.fuzzerTestOneInput(FileUtils.readFileToByteArray(file));
	}

	private static Stream<Arguments> files() {
		Collection<File> files = new ArrayList<>();
		File[] corpuses = new File(".").
				listFiles((FilenameFilter) new PrefixFileFilter("corpus"));
		assertNotNull(corpuses);

		for (File corpus : corpuses) {
			files.addAll(FileUtils.listFiles(corpus,
					// all files
					TrueFileFilter.TRUE,
					// but exclude ".svn" or ".git" directory
					EXCLUDE_FILE_FILTER));
		}

		// wrap in TreeSet to have a sorted list
		return new TreeSet<>(files).stream()
				.filter(file -> !EXCLUDED.contains(file.getName()))
				.map(Arguments::of);
	}
}
