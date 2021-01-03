#!/bin/sh

java -Xms1024m -Xmx1024m -XX:+UseG1GC -jar rcallerservice.jar --port=8080
