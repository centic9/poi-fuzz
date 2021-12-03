#!/bin/sh
#
#
# Small helper script to produce a coverage report when executing the fuzz-model
# against the current corpus.
#
# You need to enable the test in class CorpusCoverageTest
#

set -eu


# Remove any previous execution and make sure testing is triggered fully
./gradlew clean


# Execute the test with JaCoCo enabled
./gradlew check


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


# Finally create the JaCoCo report
java -jar /opt/poi/lib/util/jacococli.jar report build/jacoco/test.exec \
 --classfiles build/poifiles \
 --sourcefiles /opt/apache/poi/dist/release/maven/poi/poi-5.1.0-sources.jar:/opt/apache/poi/dist/release/maven/poi-ooxml/poi-ooxml-5.1.0-sources.jar:/opt/apache/poi/dist/release/maven/poi-ooxml-lite/poi-ooxml-lite-5.1.0-sources.jar:/opt/apache/poi/dist/release/maven/poi-scratchpad/poi-scratchpad-5.1.0-sources.jar:/opt/apache/poi/dist/xmlbeans/release/maven/xmlbeans-5.0.2-sources.jar \
 --html build/reports/jacoco


echo All Done, report is at build/reports/jacoco/index.html