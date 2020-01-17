#!/bin/bash

. build.sh

IMAGE_NAME=gitlab.dellin.ru:5005/forecast/rcallerservice/kotlin
IMAGE_VERSION=1.1.7

LOCAL_PROXY=http://10.126.6.77:3128

docker build --build-arg http_proxy=${LOCAL_PROXY} --build-arg https_proxy=${LOCAL_PROXY} -t ${IMAGE_NAME}:${IMAGE_VERSION} .
docker push ${IMAGE_NAME}:${IMAGE_VERSION}
docker image tag ${IMAGE_NAME}:${IMAGE_VERSION} ${IMAGE_NAME}:latest
docker push ${IMAGE_NAME}:latest
