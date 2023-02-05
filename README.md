This is a small project for fuzzing [Apache POI](https://poi.apache.org/) with the [jazzer](https://github.com/CodeIntelligenceTesting/jazzer/) fuzzing tool.

See [Fuzzing](https://en.wikipedia.org/wiki/Fuzzing) for a general description of the theory behind fuzzy testing.

Because Java uses a runtime environment which does not crash on invalid actions of an 
application (unless native code is invoked), Fuzzing of Java-based applications  
focuses on the following:

* verify if only expected exceptions are thrown
* verify any JNI or native code calls 
* find cases of unbounded memory allocations

Apache POI does not use JNI or native code, therefore the fuzzing target mainly
tries to trigger unexpected exceptions and unbounded memory allocations.

# Note

Currently the build.gradle file is usually configured to use POI jars from a local 
build as otherwise some unexpected types of Exceptions are still thrown in current
release 5.2.3. 

You can adjust build.gradle to point to your local build to run fuzzing without
triggering such exceptions.

This should not be necessary any more with the version of Apache POI after 5.2.3.

# How to fuzz

Build the fuzzing target:

    ./gradlew shadowJar

Download the corpus of test-files from Apache POI sources

    svn co https://svn.apache.org/repos/asf/poi/trunk/test-data corpus

You can add more documents to the corpus to help Jazzer in producing "nearly" 
proper documents which will improve fuzzing a lot. Slightly broken documents
seem to be a good seed for fuzzing as well.

Download Jazzer from the [releases page](https://github.com/CodeIntelligenceTesting/jazzer/releases), 
choose the latest version and select the file `jazzer-<os>-<version>.tar.gz`

Unpack the archive:

    tar xzf jazzer-*.tar.gz

Invoke the fuzzing:

    ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.Fuzz -rss_limit_mb=4096 corpus

In this mode Jazzer will stop whenever it detects an unexpected exception 
or crashes.

You can use `--keep_going=10` to report a given number of exceptions before stopping.

See `./jazzer` for options which can control details of how Jazzer operates.

# Fuzzing via Jazzer Docker image

You can use a ready-made Docker image for running the fuzzing in a container.

This shields the fuzzing from performing unexpected actions on your local machine
or allows to run it in a container-based infrastructure.

    docker run -v `pwd`:/fuzzing cifuzz/jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.Fuzz -rss_limit_mb=4096 corpus

# Fuzzing single file formats

## HSLF

    mkdir corpusHSLF
    cp corpus/slideshow/*.ppt corpusHSLF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzHSLF -rss_limit_mb=4096 corpusHSLF

## HSSF

    mkdir corpusHSSF
    cp corpus/spreadsheet/*.xls corpusHSSF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzHSSF -rss_limit_mb=4096 corpusHSSF

## HWPF

    mkdir corpusHWPF
    cp corpus/document/*.doc corpus/document/*.DOC corpusHWPF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzHWPF -rss_limit_mb=4096 corpusHWPF

## OldExcel

    mkdir corpusOldExcel
    cp corpus/spreadsheet/*.xls corpus/spreadsheet/*.bin corpusOldExcel/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzOldExcel -rss_limit_mb=4096 corpusOldExcel

## XSLF

    mkdir corpusXSLF
    cp corpus/slideshow/* corpusXSLF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzXSLF -rss_limit_mb=4096 corpusXSLF

## XSSF

    mkdir corpusXSSF
    cp corpus/spreadsheet/* corpusXSSF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzXSSF -rss_limit_mb=4096 corpusXSSF

## XLSX2CSV

Can re-use XLSX-corpus as the same files are processed here

    mkdir corpusXSSF
    cp corpus/spreadsheet/* corpusXSSF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzXLSX2CSV -rss_limit_mb=4096 corpusXSSF

## XWPF

    mkdir corpusXWPF
    cp corpus/document/* corpusXWPF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzXWPF -rss_limit_mb=4096 corpusXWPF

## Visio

    mkdir corpusVisio
    cp corpus/diagram/* corpusVisio/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzVisio -rss_limit_mb=4096 corpusVisio

## HDGF

    mkdir corpusHDGF
    cp corpus/diagram/*.vsd corpusHDGF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzHDGF -rss_limit_mb=4096 corpusHDGF

## HPSF

    mkdir corpusHPSF
    cp corpus/hpsf/* corpusHPSF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzHPSF -rss_limit_mb=4096 corpusHPSF

## HMEF

    mkdir corpusHMEF
    cp -a corpus/hmef corpusHMEF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzHMEF -rss_limit_mb=4096 corpusHMEF

## HPBF

    mkdir corpusHPBF
    cp -a corpus/publisher corpusHPBF/
    ./gradlew shadowJar && ./jazzer --cp=build/libs/poi-fuzz-all.jar --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** --target_class=org.dstadler.poi.fuzz.FuzzHPBF -rss_limit_mb=4096 corpusHPBF

# Fuzzing with locally compiled Apache POI libraries

If you want to test with a more recent version of Apache POI, you can add 
locally compiled jars of Apache POI to the front `--cp` commandline argument (delimited with colon ':')

Another possibility is to switch to the local files in `build.gradle`

When using IntelliJ IDEA there is a ready made run-configuration, just adjust
the path to the jar-files accordingly.

# Detected issues

* Many of the exceptions are not declared as being thrown in the JavaDoc of Apache POI,
  thus making it hard to write proper defensive try-catches when using Apache POI, for now
  you will need to catch `RuntimeException` and any declared ones.
* The RuntimeExceptions do not have a common base class which would allow to
  only catch this one type of exception to capture all expected ones
* Some places throw an IllegalArgumentException or IllegalStateException where a dedicated RuntimeException 
  would be preferable. This currently leads to a very general catching of these exceptions in the fuzz target.
* Some memory allocations are not bounded, i.e. causing out-of-memory on certain
  broken documents. Some more memory guards need to be put in place

# License

Copyright 2021-2023 Dominik Stadler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
