#!/usr/bin/env bash

sbt "project api" stage

VERSION=$(git describe --tags)

DOCKER_IMAGE="gimbalabs/balcony-api:${VERSION}"

echo "Building image: ${DOCKER_IMAGE}"

docker buildx use x86
docker buildx build --platform linux/amd64 --load -t "${DOCKER_IMAGE}" -f api/Dockerfile .
docker push "${DOCKER_IMAGE}"
