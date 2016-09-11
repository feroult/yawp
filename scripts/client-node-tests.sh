#!/bin/bash -e

MAVEN_ARGS="-Dyawp.port=8081 -Dyawp.shutdownPort=8331 -Pit"
unset MAVEN_OPTS

before() {
    cd yawp-testing/yawp-testing-appengine/

    echo "stopping devserver..."
    mvn yawp:devserver_stop $MAVEN_ARGS
    echo "done."

    echo "starting devserver..."
    mvn yawp:devserver $MAVEN_ARGS &
    mvn yawp:devserver_wait $MAVEN_ARGS
    echo "done."

    cd ../..
}

after() {
    cd yawp-testing/yawp-testing-appengine/

    echo "stopping devserver..."
    mvn yawp:devserver_stop $MAVEN_ARGS
    echo "done."

    cd ../..
}

run() {
    cd yawp-js/yawp

    npm install
    npm run build
    npm run ci

    cd ../..
}

before
run
after
