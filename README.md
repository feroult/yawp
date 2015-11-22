# YAWP!

Simple, Elegant and Powerful APIs

[![Build Status](https://travis-ci.org/feroult/yawp.svg)](https://travis-ci.org/feroult/yawp)

## Introduction

__YAWP!__'s main purpose is to help developers create meaningful APIs to support their REST based applications. It supports Google App Engine and PostgreSQL environments.

You create your POJOs and __YAWP!__

## Guides

Here you can find the complete [__YAWP!__ Guides](http://yawp.io/guides).

If you want a hands-on tutorial to start with, try the [Todo App List Tutorial](http://yawp.io/guides/tutorials/todo-list-app).

## IRC

If you want to talk about yawp, go to channel __#yawp__ at __chat.freenode.net__

## How it Works

From a single class annotation, it provides a full REST url schema with a fluent programatic API for Java and Javascript. You write your client side code the same way you do for your server side. It also provides a convenient way to organize your server side business logic.

### REST Schema

Annotate your POJO:
```java
@Endpoint(path = "/people")
public class Person {
    private IdRef<Person> id;
    private String name;
}
```

Then use one of HTTP, Java or Javascript APIs to access your resources:

__HTTP__:

| Verb        | Path           | Action                |
| ----------- |--------------- | --------------------- |
| GET         | /people        | List people           |
| POST        | /people        | Create a person       |
| GET         | /people/{id}   | Show a person         |
| PUT/PATCH   | /people/{id}   | Update a person       |
| DELETE      | /people/{id}   | Destroy a person      |

__Javascript__:
```javascript
yawp('/people').list( function(people) {} );

yawp('/people').create({ name : 'Janes' }).done( function(person) {} );

yawp(personId).fetch( function(person) {} );

yawp.update(person).done( function(person) {} );

yawp.destroy(personId).done( function(personId) {} );
```

__Java__:
```java
List<Person> people = yawp(Person.class).list();

yawp.save(new Person("Janes"));

Person person = yawp(Person.class).fetch(personId);

yawp.save(person);

yawp.destroy(person.getId());
```

### Endpoint Features

__YAWP!__ has special places to add custom server side business logic to your model, so you can create real world
applications with more specific needs.

The way __YAWP!__ deals with this is by allowing you to extend the default repository actions through
[__Features__](http://yawp.io/guides/api/actions). You can create four kind of features for your objects: __Actions__, __Transformers__, __Shields__ and __Hooks__.

## Credits

* Fernando Ultremare - feroult@gmail.com
* Luan Nico - luannico27@gmail.com
* Guilherme Carreiro - karreiro@gmail.com
* Paulo Victor Martins - paulovmr@gmail.com
