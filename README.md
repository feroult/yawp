# YAWP! Framework

A very simple and elegant domain specific language / framework in Java that helps you to create amazing APIs.

[![Build Status](https://travis-ci.org/feroult/yawp.svg)](https://travis-ci.org/feroult/yawp)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.yawp/yawp/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.yawp/yawp/)
[![Join the chat at https://gitter.im/feroult/yawp](https://badges.gitter.im/feroult/yawp.svg)](https://gitter.im/feroult/yawp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Introduction

__YAWP!__'s main purpose is to help developers to easily create meaningful and
scalable APIs to back their REST based applications. It natively supports and
implements effective usage patterns for the Google Appengine Platform, so you can
bootstrap your API very quickly with a free (to start) auto-scalable environment.

If scalability is not an issue or you'd rather do-it-yourself, you can use the
PostgreSQL persistence driver to deploy your code on different platforms.

It is that simple, you create your POJOs and __YAWP!__

## Guides

Here you can find the complete [__YAWP!__ Guides](http://yawp.io/guides).

## Getting Started

1. At the command prompt, create a new YAWP! API application:

        $ mvn archetype:generate \
            -DarchetypeGroupId=io.yawp \
            -DarchetypeArtifactId=yawp \
            -DarchetypeVersion=LATEST \
            -DgroupId=yawpapp \
            -DartifactId=yawpapp \
            -Dversion=1.0-SNAPSHOT            

2. Change directory to `yawpapp` and start the yawp development server:

        $ cd yawpapp
        $ mvn yawp:devserver

3. Using a browser, go to `http://localhost:8080/api` to check if everything is OK.

4. Using a scaffolder, create a simple endpoint model:

        $ mvn yawp:endpoint -Dmodel=person

    **Output:**

    ``` java
    @Endpoint(path = "/people")
    public class Person {
        @Id
        IdRef<Person> id;
    }    
    ```
    **Try it:**

        $ curl http://localhost:8080/api/people

5. Follow the guidelines to start developing your API:
    * [Your First API](http://yawp.io/guides/getting-started/your-first-api)
    * [Todo App List Tutorial](http://yawp.io/guides/tutorials/todo-list-app)
    * [API Documentation](http://yawp.io/guides/api/models)    

## Contributing

Everyone willing to contribute with YAWP! is welcome. To start developing you
will need an environment with:

* JDK 1.7+
* Maven 3.3+
* PostgreSQL 9.4+

Then follow the [travis-ci build script](../master/.travis.yml) to get your build working.

## IRC

Feel free to contact the developers at the IRC channel __#yawp__ at __chat.freenode.net__

## License

YAWP! is released under the [MIT license](https://opensource.org/licenses/MIT).
