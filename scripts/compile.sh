#!/usr/bin/env sh
set -eu

rm -rf out
mkdir -p out
find src -name "*.java" | xargs javac -d out
