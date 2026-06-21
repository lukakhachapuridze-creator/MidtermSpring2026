#!/usr/bin/env sh
set -eu

scripts/compile.sh

if [ -f ./mvnw ]; then
  MVN=./mvnw
else
  MVN=mvn
fi

$MVN -q exec:java -Dexec.args="$*"
