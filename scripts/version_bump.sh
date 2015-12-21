#!/bin/bash
VERSION=$1
mvn versions:set -DnewVersion=$VERSION; mvn versions:commit
git commit -am "version bump"
git push
