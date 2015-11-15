#!/bin/bash -e

if [ $# -ne 1 ]; then
    echo "use: smoke-tests.sh YAWP_VERSION"
    exit 1
fi

VERSION=$1
WORKING_DIR=.working/smoke-tests
MAVEN_ARGS="-Dyawp.port=8081 -Dyawp.shutdownPort=8331"
unset MAVEN_OPTS

before() {
    rm -rf $WORKING_DIR
    mkdir -p $WORKING_DIR
    cd $WORKING_DIR
    echo "working dir $WORKING_DIR - yawp version $VERSION"

    mvn archetype:generate \
       -DarchetypeGroupId=io.yawp \
       -DarchetypeArtifactId=yawp \
       -DarchetypeVersion=$VERSION \
       -DgroupId=yawpapp \
       -DartifactId=yawpapp \
       -Dversion=1.0-SNAPSHOT \
       --batch-mode

    cd yawpapp
}

after() {
    cd ../../..
    rm -rf $WORKING_DIR

    echo "success!"
}

run_scaffolds() {
    echo "runnning scaffolds..."

    mvn yawp:endpoint -Dmodel=person
    mvn yawp:action -Dmodel=person -Dname=dummy
    mvn yawp:transformer -Dmodel=person -Dname=dummy
    mvn yawp:hook -Dmodel=person -Dname=dummy

    mvn clean install
}

run_smoke_tests() {
    echo "running smoke tests"

    echo "stopping devserver..."
    mvn yawp:devserver_stop $MAVEN_ARGS
    echo "done."

    echo "starting devserver..."
    mvn yawp:devserver $MAVEN_ARGS &
    mvn yawp:devserver_wait $MAVEN_ARGS
    echo "done."

    echo "calling endpoint"
    curl -v -H "Content-type: application/json" -X POST -d "{}" http://localhost:8081/api/people; echo
    curl -v http://localhost:8081/api/people/1; echo
    curl -v http://localhost:8081/api/people/1/dummy; echo

    echo "stopping devserver..."
    mvn yawp:devserver_stop $MAVEN_ARGS
    echo "done."

    ls target/appengine-generated/local_db.bin
}

run() {
    run_scaffolds
    run_smoke_tests
}

before
run
after
