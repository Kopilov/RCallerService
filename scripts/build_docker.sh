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

IMAGE_NAME=kopilov/rcallerservice
IMAGE_VERSION=1.1.1

#LOCAL_PROXY=http://10.126.6.77:3128

#docker build --build-arg http_proxy=${LOCAL_PROXY} --build-arg https_proxy=${LOCAL_PROXY} -t ${IMAGE_NAME}:${IMAGE_VERSION} .
docker build --build-arg IMAGE_VERSION=${IMAGE_VERSION} -t ${IMAGE_NAME}:base-${IMAGE_VERSION} -f Dockerfile-base .
docker build --build-arg IMAGE_VERSION=${IMAGE_VERSION} -t ${IMAGE_NAME}:${IMAGE_VERSION} -f Dockerfile-build_multistage ..
docker build --build-arg IMAGE_VERSION=${IMAGE_VERSION} -t ${IMAGE_NAME}:forecast-${IMAGE_VERSION} -f Dockerfile-forecast .
#docker push ${IMAGE_NAME}:${IMAGE_VERSION}
#docker image tag ${IMAGE_NAME}:${IMAGE_VERSION} ${IMAGE_NAME}:latest
#docker push ${IMAGE_NAME}:latest
