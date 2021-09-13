#!/bin/bash

#get script placing directory
PRG="$0"
while [ -h "$PRG" ]; do
    ls="$(ls -ld "$PRG")"
    link="${ls##*-> }" # remove largest prefix: yields link target (behind ->)
    if [ "$link" != "${link#/}" ]; then # remove prefix / if present
        # path was absolute
        PRG="$link"
    else
        # was not
        PRG="$(dirname "$PRG")/$link"
    fi
done

DIR="$(dirname "$PRG")"

cd "${DIR}"

rm -f rcallerservice.jar
../gradlew -b ../build.gradle clean
../gradlew -b ../build.gradle build
find ../rcallerservice/build -name "rcallerservice*jar" -exec cp {} ./rcallerservice.jar \;
