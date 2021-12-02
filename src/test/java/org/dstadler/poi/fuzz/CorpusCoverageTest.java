package org.dstadler.poi.fuzz;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A simple test which runs all files from the corpus to be
 * able to see where we are missing coverage, i.e. which types
 * of corpus files are missing.
 *
 * Disabled as it can run for a long time.
 */
public class CorpusCoverageTest {
	private static final Set<String> EXCLUDED = Set.of();

	@Disabled("Can run a long time when there is a large corpus used for fuzzing")
	@ParameterizedTest
	@MethodSource("provideStringsForIsBlank")
	void testCorpusFile(File file) throws IOException {
		System.out.println("Running file " + file);
		try {
			Fuzz.fuzzerTestOneInput(FileUtils.readFileToByteArray(file));
		} catch (RuntimeException | OutOfMemoryError | AssertionError | StackOverflowError e) {
			// ignore any problem in the corpus as we just want to cap
		}
	}

	private static Stream<Arguments> provideStringsForIsBlank() {
		Collection<File> files = FileUtils.listFiles(new File("corpus"),
				// all files
				TrueFileFilter.TRUE,
				// but exclude ".svn" or ".git" directory
				new NotFileFilter(
						new OrFileFilter(
							new NameFileFilter(".svn"),
							new NameFileFilter(".git")
						)
				));

		// wrap in TreeSet to have a sorted list
		return new TreeSet<>(files).stream()
				.filter(file -> !EXCLUDED.contains(file.getName()))
				.map(Arguments::of);
	}
}
