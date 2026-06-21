#!/usr/bin/env sh
set -eu

if [ -f ./mvnw ]; then
  ./mvnw -q compile
else
  mvn -q compile
fi
