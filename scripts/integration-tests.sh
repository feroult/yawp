#!/bin/bash

DRIVER=$1

MAVEN_ARGS="-Dyawp.port=8081 -Dyawp.shutdownPort=8331 -P it"
unset MAVEN_OPTS

cd ../yawp-$DRIVER

echo "stopping devserver..."
mvn yawp:devserver_stop $MAVEN_ARGS
echo "done."

echo "starting devserver..."
mvn yawp:devserver $MAVEN_ARGS &
mvn yawp:devserver_wait $MAVEN_ARGS
echo "done."

phantomjs ../scripts/runner.js http://localhost:8081/test/all.html
STATUS=$?

echo "stopping devserver..."
mvn yawp:devserver_stop $MAVEN_ARGS
echo "done."

exit $STATUS
