#!/bin/sh

MAVEN_OPTS=-Djava.awt.headless=true
DATASTORE_FILE=../target/integration-tests-db.bin
JVM_FLAGS="-Dappengine.jvmFlags.datastore.default_high_rep_job_policy_unapplied_job_pct=0"
DEVSERVER_ARGS="-Djava.awt.headless=true -Dmaven.test.skip=true $JVM_FLAGS -Dappengine.port=8081 -Ddatastore.backing_store=$DATASTORE_FILE"

rm -f $DATASTORE_FILE

echo "starting devserver... done"
(cd ..; mvn appengine:devserver_stop $DEVSERVER_ARGS)
(cd ..; mvn appengine:devserver_start $DEVSERVER_ARGS)
echo ' done'

./runner.py ../src/test/webapp/test/all.html
STATUS=$?

(cd ..; mvn appengine:devserver_stop $DEVSERVER_ARGS)
echo "stopping devserver... done"

exit $STATUS
