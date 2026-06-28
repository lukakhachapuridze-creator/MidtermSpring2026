#!/usr/bin/env sh
set -eu

if [ -f ./mvnw ]; then
  ./mvnw -q test
else
  mvn -q test
fi
