#!/bin/bash

rm -f rcallerservice.jar
../gradlew -b ../build.gradle clean
../gradlew -b ../build.gradle build
find ../build -name "rcallerservice*jar" -exec cp {} ./rcallerservice.jar \;
