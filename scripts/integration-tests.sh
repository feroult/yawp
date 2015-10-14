#!/bin/bash

MAVEN_ARGS="-Dmaven.test.skip=true -Dyawp.port=8081 -Dyawp.shutdownPort=8331"
unset MAVEN_OPTS

echo "stopping devserver..."
(cd ../yawp-appengine; mvn yawp:devserver_stop $MAVEN_ARGS)
echo "done."

echo "starting devserver..."
(cd ../yawp-appengine; mvn yawp:devserver $MAVEN_ARGS &)
sleep 5
echo "done."

phantomjs runner.js http://localhost:8081/test/all.html
STATUS=$?

echo "stopping devserver..."
(cd ../yawp-appengine; mvn yawp:devserver_stop $MAVEN_ARGS)
echo "done."

exit $STATUS
