# Welcome to YAWP!

A very simple and elegant domain specific language and framework in Java that helps you to create amazing APIs.

[![Build Status](https://travis-ci.org/feroult/yawp.svg)](https://travis-ci.org/feroult/yawp)

## Introduction

__YAWP!__'s main purpose is to help developers to easily create meaningful APIs to
back their REST based applications. It natively supports and abstracts the Google
Appengine Platform, so you can bootstrap your API very quickly with a free
(to start) auto-scalable environment. Later, if you change your mind, there are no
lock-ins with Google and you can move freely to any cloud platform you want without
losing data or having to rewrite your code.

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

5. Follow the guidelines to start developing your API:
    * [Your First API](http://yawp.io/guides/getting-started/your-first-api)
    * [Todo App List Tutorial](http://yawp.io/guides/tutorials/todo-list-app)
    * [YAWP! Guides](http://yawp.io/guides)
    * [The API Documentation](http://yawp.io/guides/api/models)    

## Contributing

Everyone willing to contribute with YAWP! is welcome. To start developing you
will need an environment with:

* JDK 1.7+
* Maven 1.3+
* PostgreSQL 9.4+

## IRC

Feel free to contact the developers at the IRC channel __#yawp__ at __chat.freenode.net__

## License

YAWP! is released under the [MIT license](https://opensource.org/licenses/MIT).
