#!/bin/sh

java -Xms1024m -Xmx1024m -jar rcallerservice.jar --port=8080 "${@}"
