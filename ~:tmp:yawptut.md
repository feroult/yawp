# YAWP! Tutorial

## The easiest way to bootstrap an YAWP! API is to create a project from it's maven archetype:

### 1) Basic Setup

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

```
curl -H "Content-type: application/json" -X POST -d "{ name: 'daniel' }"  http://localhost:8080/api/people ; echo
curl -H "Content-type: application/json" -X GET http://localhost:8080/api/people ; echo
```

### 2) Rest Actions

If you check inside te src/main folder of the generated app, you'll find a package yawptut.models and inside a java file called Person.java. This is the basic structure of an YAWP! Endpoint:

```java
@Endpoint(path = "/people")
public class Person
```

With this you have all the basic rest operations:

```bash
curl -H "Content-type: application/json" -X POST -d "{ id: '/people/1', name: 'daniel' }"  http://localhost:8080/api/people ; echo
curl -H "Content-type: application/json" -X GET http://localhost:8080/api/people ; echo
curl -H "Content-type: application/json" -X GET http://localhost:8080/api/people/1 ; echo
curl -H "Content-type: application/json" -X PUT -d "{ id: '/people/1', name: 'chnaged daniel' }"  http://localhost:8080/api/people/1 ; echo
curl -H "Content-type: application/json" -X DELETE http://localhost:8080/api/people/1 ; echo
```

### 3) Add a custom Action
