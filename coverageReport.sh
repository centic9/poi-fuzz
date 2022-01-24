#!/bin/sh
#
#
# Small helper script to produce a coverage report when executing the fuzz-model
# against the current corpus.
#
# You need to enable the test in class CorpusCoverageTest
#

set -eu


# Build the fuzzer and fetch dependency-jars
./gradlew shadowJar getDeps


# extract class-files of Apache POi
mkdir -p build/poifiles
cd build/poifiles
for i in `find /opt/apache/poi/dist/release/maven/ -type f | grep .jar | grep -v -- -sources.jar | grep -v -- -javadoc.jar | grep -v -- .sha512 | grep -v -- .sha256 | grep -v -- .asc | grep -v poi-ooxml-full | grep -v poi-integration | grep -v poi-examples | grep -v poi-excelant`; do
  echo $i
  unzip -o -q $i
done


# Remove some packages that we do not want to include in the Report
rm -r com
rm -r org/openxmlformats
rm -r org/etsi
rm -r org/w3

cd -



# Fetch JaCoCo Agent
test -f jacoco-0.8.7.zip || wget --continue https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.7/jacoco-0.8.7.zip
unzip -o jacoco-0.8.7.zip lib/jacocoagent.jar
mv lib/jacocoagent.jar build/
rmdir lib

mkdir -p build/jacoco


# Run Jazzer with JaCoCo-Agent to produce coverage information
./jazzer \
  --cp=build/libs/poi-fuzz-all.jar \
  --instrumentation_includes=org.apache.commons.** \
  --target_class=org.dstadler.poi.fuzz.Fuzz \
  --nohooks \
  --jvm_args="-XX\\:-OmitStackTraceInFastThrow:-javaagent\\:build/jacocoagent.jar=destfile=build/jacoco/corpus.exec" \
  -rss_limit_mb=8192 \
  -runs=0 \
  corpus


# Finally create the JaCoCo report
java -jar /opt/poi/lib/util/jacococli.jar report build/jacoco/corpus.exec \
 --classfiles build/poifiles \
 --classfiles build/classes/java/main \
 --sourcefiles /opt/apache/poi/dist/release/maven/poi/poi-5.1.0-sources.jar:/opt/apache/poi/dist/release/maven/poi-ooxml/poi-ooxml-5.1.0-sources.jar:/opt/apache/poi/dist/release/maven/poi-ooxml-lite/poi-ooxml-lite-5.1.0-sources.jar:/opt/apache/poi/dist/release/maven/poi-scratchpad/poi-scratchpad-5.1.0-sources.jar:/opt/apache/poi/dist/xmlbeans/release/maven/xmlbeans-5.0.2-sources.jar \
 --sourcefiles src/main/java \
 --html build/reports/jacoco


echo All Done, report is at build/reports/jacoco/index.html