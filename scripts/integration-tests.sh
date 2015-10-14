#!/bin/bash

MAVEN_ARGS="-Dyawp.port=8081 -Dyawp.shutdownPort=8331"
unset MAVEN_OPTS

echo "test!"

cd ../yawp-appengine

echo "stopping devserver..."
mvn yawp:devserver_stop $MAVEN_ARGS
echo "done."

echo "starting devserver..."
mvn yawp:devserver $MAVEN_ARGS &
sleep 5
echo "done."

exit 0

# phantomjs runner.js http://localhost:8081/test/all.html
# STATUS=$?
#
# echo "stopping devserver..."
# (cd ../yawp-appengine; mvn yawp:devserver_stop $MAVEN_ARGS)
# echo "done."
#
# exit $STATUS
