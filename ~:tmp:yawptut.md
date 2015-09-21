This is the shortest YAWP! Tutorial ever made.

### 1 - Basic Setup

The easiest way to bootstrap an YAWP! API is to create a project from it's maven archetype:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.yawp \
  -DarchetypeArtifactId=yawp \
  -DarchetypeVersion=1.3-SNAPSHOT \
  -DgroupId=yawptut \
  -DartifactId=yawptut \
  -Dversion=1.0-SNAPSHOT
```

Then, start the devserver:

```bash
cd yawptut
mvn appengine:devserver
```

If everything went without errors, you should have an example API to test:

```bash
curl -H "Content-type: application/json" -X POST -d "{ name: 'daniel' }"  http://localhost:8080/api/people ; echo
curl -H "Content-type: application/json" -X GET http://localhost:8080/api/people ; echo
```

### 2 - Rest Actions

If you check inside te src/main folder of the generated app, you'll find a package yawptut.models and inside a java file called Person.java. This is the basic structure of an YAWP! Endpoint:

```java
@Endpoint(path = "/people")
public class Person {

    @Id
    private IdRef<Person> id;

    @Index
    private String name;

    public IdRef<Person> getId() {
        return id;
    }

    public void setId(IdRef<Person> id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
```

With this you have all the basic rest operations:

```bash
curl -H "Content-type: application/json" -X POST -d "{ id: '/people/1', name: 'daniel' }"  http://localhost:8080/api/people ; echo
curl -H "Content-type: application/json" -X GET http://localhost:8080/api/people ; echo
curl -H "Content-type: application/json" -X GET http://localhost:8080/api/people/1 ; echo
curl -H "Content-type: application/json" -X PUT -d "{ id: '/people/1', name: 'chnaged daniel' }"  http://localhost:8080/api/people/1 ; echo
curl -H "Content-type: application/json" -X DELETE http://localhost:8080/api/people/1 ; echo
```

### 3 - Javascript Client

YAWP! also provides a Javascript client, all http actions are supported. To test it, create a html with this simple structure:

```html
<html>
    <head>
        <meta charset="utf-8">
        <title>YAWP! js client</title>

        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script src="https://rawgit.com/feroult/yawp/master/yawp-core/src/main/js/yawp.js"></script>
    </head>
    <body>
    </body>
</html>
```

Then open your javascript console and type:

```javascript
yawp('/people').create({ id: '/people/1', name: 'daniel' }).done(function() {
    yawp('/people/1').fetch(function(person) { console.log('person:', person.name); });
});
```

The output should be:

```javascript
person: daniel
```

From now on we will only use the javascript client to run the examples.

### 3 - Adding custom actions

Inside your editor go to the package yawptut and create a package called **actions**. Than add a class called PersonAction, like this:

```java
public class PersonAction extends Action<Person> {

    @PUT("upper")
    public void upper(IdRef<Person> id) {
        Person person = id.fetch();
        person.setName(person.getName().toUpperCase());
        yawp.save(person);
    }
}
```

And then call the action:

```javascript
yawp('/people/1').put('upper').done(function(response) {
    console.log('response:', response);
});
```

Check the result:

```bash
curl -H "Content-type: application/json" -X GET http://localhost:8080/api/people/1 ; echo
```

### 4 - Creating relationships between Endpoints

To create simple relationship, go to your **yawptut.models** package and create a **Job** class:

```java
@Endpoint(path = "/jobs")
public class Job {

    @Id
    private IdRef<Job> id;

    @Index
    private String name;

    public IdRef<Job> getId() {
        return id;
    }

    public void setId(IdRef<Job> id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

Go to your **Person** model and add a relationship property:

```
(...)
private IdRef<Job> jobId;

public IdRef<Job> getJobId() {
    return jobId;
}

public void setJobId(IdRef<Job> jobId) {
    this.jobId = jobId;
}
(...)
```

Now you should be able to work with relationships like this:

```bash
curl -H "Content-type: application/json" -X POST -d "{ id: '/people/1', name: 'daniel' }"  http://localhost:8080/api/jobs ; echo
```

### 5 - Protecting you API

To protect your API create a Shield. Go to the yawptut package again and create a subpackage called **shields**. Then add a java class called PersonShield.java:

```java
public class PersonShield extends Shield<Person> {

    @Override
    public void show(IdRef<Person> id) {
        allow();
    }

}
```

Now, you'll only be able to show a person. Check if you can create another person:

```bash
curl -H "Content-type: application/json" -X POST -d "{ name: 'james' }"  http://localhost:8080/api/people ; echo
```

The result should be:

```javascript
{"status":"error","message":"The resquest was not allowed by the endpoint shield yawptut.shields.PersonShield"}
```



### 7 - Testing

Your project also comes with a preconfigured qunit test suite. To run it, first open your Endpoint to all actions. Change the class **yawptut.PersonShield** to this:


```java
public class PersonShield extends Shield<Person> {

    @Override
    public void defaults() {
        allow();
    }
}
```

Go to a web browser and point to this address: [http://localhost:8080/test/all.html](http://localhost:8080/test/all.html)


### 8 - Other APIs

Yawp also come with another 2 APIs, that you should be using further as your API grows, they are: Hooks and Transformer APIs. At the project home (http://github.com/feroult/yawp) you can get more information on how to use them.
