#!/bin/bash

if [ $# -ne 1 ]; then
    echo "use: archetype-tests.sh YAWP_VERSION"
    exit 1
fi

VERSION=$1
WORK_DIR=.working/archetype-tests

before() {
    mkdir -p $WORK_DIR
    cd $CD_WORK_DIR
    pwd
    exit 1

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
    cd ../..
    #rm -rf $WORK_DIR
}

run() {
    mvn yawp:endpoint -Dmodel=person
    mvn yawp:action -Dmodel=person -Dname=dummy
    mvn yawp:transformer -Dmodel=person -Dname=dummy
    mvn yawp:hook -Dmodel=person -Dname=dummy

    mvn clean install
    ls target/*.bin
}

before
run
after
