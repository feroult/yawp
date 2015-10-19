#!/bin/bash

VERSION=$1
echo "releasing: $VERSION"
mvn versions:set -DnewVersion=$VERSION
mvn versions:commit
git commit -am "release $VERSION"
# check if it is not a snapshot
git tag "yawp-$VERSION"
git push --tags
#killall -9 gpg-agent
#eval "$(gpg-agent --daemon)"
mvn clean deploy -Prelease
