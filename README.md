# YAWP!

A lightweight REST API framework focused on productivity and scalability. 

The power of Java and Google Appengine for the server, the flexibility of Javascript for the client.

[![Build Status](https://travis-ci.org/feroult/yawp.svg)](https://travis-ci.org/feroult/yawp)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.yawp/yawp/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.yawp/yawp/)
[![Join the chat at https://gitter.im/feroult/yawp](https://badges.gitter.im/feroult/yawp.svg)](https://gitter.im/feroult/yawp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Introduction

Everything happens around a plain Java object:

``` java
@Endpoint(path = "/people")
public class Person {
    @Id
    IdRef<Person> id;             
    String name;
}    
```

It gives you a complete set of REST APIs under its endpoint:

* Create
* Update/Patch
* Destroy
* List/Queries

Access it from Node.js or a browser with Javascript:
 
``` javascript
var yawp = require('yawp');
 
var promise = yawp('/people').create({name: 'janes'}).then(function(person) {
    console.log('created', person.id);
   
    person.name = 'janes joplin';
    return person.save(function() {
        console.log('updated');
    });
});
```
     
Customize your server and tune your client with extra features:

* Action Routes
* Transformers
* Security Shields
* Model Hooks
* Asynchronous Pipes

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
    * [The Javascript Client](http://yawp.io/guides/tutorials/the-javascript-client)
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
