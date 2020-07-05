#!/bin/bash

rm -f rcallerservice.jar
#export JAVA_HOME=$HOME/usr/jdk-11.0.6+10
../gradlew -b ../build.gradle clean
../gradlew -b ../build.gradle build
find ../build -name "rcallerservice*jar" -exec cp {} ./rcallerservice.jar \;
