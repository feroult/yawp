#!/bin/bash -e
mvn clean install -P it
./scripts/scaffolding-it-test.sh
