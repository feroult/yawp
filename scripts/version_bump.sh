#!/bin/bash
VERSION=$1
mvn versions:set -DnewVersion=$VERSION-SNAPSHOT; mvn versions:commit
git commit -am "version bump"
git push
