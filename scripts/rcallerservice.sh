#!/bin/bash

java -Xms1024m -Xmx1024m -XX:+UseG1GC -jar rcallerservice.jar --port=8080 1>/dev/null 2>/dev/null
