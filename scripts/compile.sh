#!/usr/bin/env sh
set -eu

rm -rf out
mkdir -p out

javac -d out src/Main.java src/CharacterizationTest.java src/model/*.java src/interfaces/*.java src/service/*.java
