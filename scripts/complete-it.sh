#!/bin/bash -e

VERSION=$(cat pom.xml | grep "<version>" | head -1 | cut -d ">" -f 2 | cut -d "<" -f 1)

echo "running complete yawp it tests on version: $VERSION"

mvn clean install -P it
./scripts/client-node-tests.sh
./scripts/smoke-tests.sh $VERSION appengine
./scripts/smoke-tests.sh $VERSION postgresql
