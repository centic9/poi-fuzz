This is a small project for fuzzing Apache POI with the [jazzer](https://github.com/CodeIntelligenceTesting/jazzer) fuzzing tool.

See [Fuzzing](https://en.wikipedia.org/wiki/Fuzzing) for a general description of the theory behind fuzzy testing.

Because Java uses a runtime environment which does not crash on invalid actions of an 
application (unless native code is invoked), Fuzzing of Java-based applications  
focuses on the following:

* verify if only expected exceptions are thrown
* verify any JNI or native code calls 

Apache POI does not use JNI or native code, therefore the fuzzing target mainly
tries to trigger unexpected exceptions.

# How to fuzz

Build the fuzzing target:

    ./gradlew shadowJar

Download the corpus of test-files from Apache POI sources

    svn co https://svn.apache.org/repos/asf/poi/trunk/test-data corpus

Download Jazzer from the [releases page](https://github.com/CodeIntelligenceTesting/jazzer/releases), 
choose the latest version and select the file `jazzer-<os>-<version>.tar.gz`

Unpack the archive:

    tar xzf jazzer-*.tar.gz

Invoke the fuzzing:

    ./jazzer --cp=build/libs/poifuzz-all.jar --target_class=org.dstadler.poi.fuzz.Fuzz -rss_limit_mb=4096 corpus

In this mode Jazzer will stop whenever it detects an unexpected exception.

See `./jazzer` for options which can control details of how Jazzer operates.


# Detected issues

* Many of the exceptions are not declared as being thrown in the JavaDoc of Apache POI,
  thus making it hard to write proper defensive try-catches when using Apache POI
* The RuntimeExceptions do not have a common base class which would allow to
  only catch this one exception to capture all expected ones
* Some places throw an IllegalArgumentException or IllegalStateException where a dedicated RuntimeException 
  would be preferable. This currently leads to a very general catching of these exceptions in the fuzz target.  
