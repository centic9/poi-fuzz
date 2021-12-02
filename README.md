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

# Fuzzing with locally compiled Apache POI libraries

If you want to test with a more recent version of Apache POI, you can add 
locally compiled jars of Apache POI to the front `--cp` commandline argument (delimited with colon ':')

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

Copyright 2021 Dominik Stadler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
