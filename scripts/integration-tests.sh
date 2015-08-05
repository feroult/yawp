#!/bin/bash

# if [ "$JAVA_HOME" = "/usr/lib/jvm/java-7-oracle/bin/java" ]; then
#   echo "Shippable bug detected!"
#   export JAVA_HOME="/usr/lib/jvm/java-7-oracle"
# fi

MAVEN_ARGS="-Dmaven.test.skip=true -Dappengine.port=8081 -Dappengine.jvmFlags.datastore.default_high_rep_job_policy_unapplied_job_pct=0"

echo "starting devserver..."
(cd ..; mvn appengine:devserver_start $MAVEN_ARGS)
echo "done."

echo "running: $PWD"
#./runner.py ../src/test/webapp/test/all.html
wget http://localhost:8081/test/all.html
cat all.html
phantomjs runner.js http://localhost:8081/test/all.html
STATUS=$?

echo "stopping devserver..."
(cd ..; mvn appengine:devserver_stop $MAVEN_ARGS)
echo "done."

exit $STATUS
