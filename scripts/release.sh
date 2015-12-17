#!/bin/bash
VERSION=$1
echo "releasing: $VERSION"
mvn versions:set -DnewVersion=$VERSION
mvn versions:commit
git commit -am "release $VERSION"

if [[ ! $VERSION =~ .*SNAPSHOT.* ]]; then
    git tag "yawp-$VERSION"
    git push --tags
fi

git push
killall -9 gpg-agent
eval "$(gpg-agent --daemon)"
mvn clean deploy -Prelease
