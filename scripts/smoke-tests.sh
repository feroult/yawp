#!/bin/bash -e

if [ $# -ne 2 ]; then
    echo "use: smoke-tests.sh YAWP_VERSION DRIVER"
    exit 1
fi

VERSION=$1
POSTGRESQL=$2
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
       -DgroupId=yawpsmoke \
       -DartifactId=yawpsmoke \
       -Dversion=1.0-SNAPSHOT \
       --batch-mode

    cd yawpsmoke

    if is_postgresql; then
        init_postgresql
    else
        init_appengine
    fi
}

is_postgresql() {
    [ -n "$POSTGRESQL" ] && [ "$POSTGRESQL" == "postgresql" ]
}

init_postgresql() {
    echo "driver set to postgresql"
    change_to_postgresql
    dropdb yawpsmoke_test || true
    dropdb yawpsmoke_development || true
}

init_appengine() {
    echo "driver set to appengine"
}

change_to_postgresql() {
    if [ "$(uname)" = "Linux" ]; then
        SED="sed -i -e"
    else
        SED="sed -i '' -e"
    fi
    $SED "s/<artifactId>yawp<\/artifactId>/<artifactId>yawp-postgresql<\/artifactId>/g" pom.xml
    $SED "s/<artifactId>yawp-testing<\/artifactId>/<artifactId>yawp-testing-postgresql<\/artifactId>/g" pom.xml
}

after() {
    cd ../../..
    rm -rf $WORKING_DIR

    echo "success!"
}

run_scaffolds() {
    echo "runnning scaffolds..."

    mvn yawp:endpoint -Dmodel=person_address
    mvn yawp:action -Dmodel=person_address -Dname=dummy
    mvn yawp:transformer -Dmodel=person_address -Dname=dummy
    mvn yawp:hook -Dmodel=person_address -Dname=dummy

    mvn clean install
}

run_smoke_tests() {
    echo "running smoke tests..."

    echo "synchronizing datastore..."
    mvn yawp:sync

    echo "stopping devserver..."
    mvn yawp:devserver_stop $MAVEN_ARGS
    echo "done."

    echo "starting devserver..."
    mvn yawp:devserver $MAVEN_ARGS &
    mvn yawp:devserver_wait $MAVEN_ARGS
    echo "done."

    echo "calling endpoint..."
    ID=$(curl -f -sS -H "Content-type: application/json" -X POST -d "{}" http://localhost:8081/api/person-addresses | sed -e 's/^.*"id":"\([^"]*\)".*$/\1/')
    echo "created person: $ID"

    curl -f -v http://localhost:8081/api$ID; echo
    curl -f -v http://localhost:8081/api$ID/dummy; echo

    echo "stopping devserver..."
    mvn yawp:devserver_stop $MAVEN_ARGS
    echo "done."

    if is_postgresql; then
        postgresql_last_check
    else
        appengine_last_check
    fi
}

postgresql_last_check() {
    echo "executing last specific postgresql driver check"
    RESULT=$(psql -d yawpsmoke_development -c "select * from person_addresses" | grep person_addresses)
    if [ -z "$RESULT" ]; then
        exit 1
    fi
}

appengine_last_check() {
    echo "executing last specific appegine driver check"
    ls target/appengine-generated/local_db.bin
}


run() {
    run_scaffolds
    run_smoke_tests
}

before
run
after
