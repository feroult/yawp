# YAWP!

Simple, Elegant and Powerful APIs

[![Build Status](https://travis-ci.org/feroult/yawp.svg)](https://travis-ci.org/feroult/yawp)

## Introduction

__YAWP!__'s main purpose is to help developers create meaningful APIs to support their REST based applications. It supports Google App Engine and PostgreSQL environments.

You create your POJOs and __YAWP!__

## Guides

Here you can find the complete [__YAWP!__ Guides](http://yawp.io/guides).

## Getting Started

1. At the command prompt, create a new YAWP! API application:

        $ mvn archetype:generate \
            -DarchetypeGroupId=io.yawp \
            -DarchetypeArtifactId=yawp \
            -DarchetypeVersion=LATES \
            -DgroupId=yawpapp \
            -DartifactId=yawpapp \
            -Dversion=1.0-SNAPSHOT            

2. Change directory to `yawpapp` and start the yawp development server:

        $ cd yawpapp
        $ mvn yawp:devserver

3. Using a browser, go to `http://localhost:8080/api/` to check if everything is OK.

5. Follow the guidelines to start developing your API. You may find
   the following resources handy:
    * [Todo App List Tutorial](http://yawp.io/guides/tutorials/todo-list-app)
    * [YAWP! Guides](http://yawp.io/guides)
    * [The API Documentation](http://yawp.io/guides/api/models)    

## Contributing

Everyone willing to contribute with YAWP! is welcome.  To start developing you
will need an environment with:

    * JDK 1.7 or newer;
    * Maven 1.3 or newer;
    * PostgreSQL 9.4 or newer;

## IRC

Feel free to contact the developers at the IRC channel __#yawp__ at __chat.freenode.net__
