#!/bin/bash

set -eu

if [ $# -gt 0 ]; then
  ONE=$1
  echo
  echo "Fuzzing only ${ONE}"
else
  ONE=
fi

echo
echo Rebuilding
./gradlew shadowJar

for i in `cd src/main/java/org/dstadler/poi/fuzz/ && ls Fuzz${ONE}*.java`; do
  CLASS=`echo $i | sed -e 's/.java//g'`
  CORPUS=`echo ${CLASS} | sed -e 's/Fuzz/corpus/g'`

  echo
  echo $i: ${CLASS} and ${CORPUS}

  #  --keep_going=5 \
  ./jazzer --cp=build/libs/poi-fuzz-all.jar \
    --instrumentation_includes=org.apache.poi.**:org.apache.xmlbeans.** \
    --target_class=org.dstadler.poi.fuzz.${CLASS} \
    -rss_limit_mb=4096 \
    -max_total_time=600 \
    ${CORPUS}
done