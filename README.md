# YAWP!

Yet Another Web API for Google App Engine

[![Build Status](https://api.shippable.com/projects/5418400b50f3833e055ab249/badge?branchName=master)](https://app.shippable.com/projects/5418400b50f3833e055ab249/builds/latest)

## Introduction

**YAWP!** is a Java framework built on top of Google App Engine, whose main purpose is to provide a simple and meaningful API to support REST based applications.

From a single class annotation, it provides a full REST url schema with a fluent progamatic API for Java and Javascript. You write your client side code the same way you do for your server side. It also creates a convenient way to organize your server side business logic through Actions, Hooks and Transformers. 

You create your POJOs and **YAWP!** 

### REST Schema

Annotate your POJO:
```java
@Endpoint(path = "/people")
public class Person {

    private String name;
    
}
```

Then use one of HTTP, Java or Javascript APIs to access your resources:

**HTTP**:

| Verb        | Path         | Action                |
| ----------- |------------- | --------------------- |
| GET         | /people      | List people           |
| POST        | /people      | Create a person       |
| GET         | /people/id   | Show a person         |
| PUT/PATCH   | /people/id   | Update a person       |
| DELETE      | /people/id   | Destroy a person      |

**Javascript**:
```javascript
yawp('/people').list( function(people) {} );

yawp('/people').create({ name : 'Janes' }).done( function(person) {} );

yawp(personId).fetch( function(person) {} );

yawp.update(person).done( function(person) {} );

yawp.destroy(personId).done( function(personId) {} );
```

**Java**:
```java
List<Person> people = yawp(Person.class).list();

yawp.save(new Person("Janes"));

Person person = yawp(Person.class).fetch(personId);

yawp.save(person);

yawp.destroy(person.getId());
```

### IdRef

The IdRef&lt;T&gt; brings a bit of innovation inside your POJOs. This class simplifies all underlying manipulation of Datastore Key mechanism and creates a type safe link beetween all your domain objects. 

To define the identity field of a domain object you need to declare an IdRef&lt;T&gt; annotated with @Id. Then you can use this identity as a reference from another domain object: 
```java
@Endpoint(path = "/people")
public class Person {

    @Id
    private IdRef<Person> id;
    
    private IdRef<Address> addressId;
    
    private String name;
    
}
```
Note: All **YAWP!** POJOs must have one and only one IdRef attribute annotated with @Id

Through the @ParentId annotation IdRef also exposes the Ancestor attribute of the Datastore Key architecuture, leveraging it's strong consistency model when necessary:
```java
@Endpoint(path = "/people")
public class Person {

    @Id
    private IdRef<Person> id;

    @ParentId
    private IdRef<Company> companyId;
    
    ...
}
```


## Features

 * Amazingly simple
 * Nice querying syntax encapsulating GAE's Query classes.
 * Nested resources using GAE Parent/Child functionality
 * Hook, Action and Transformers API

## Examples

In a very simple example, we can create the class User, with just a few fields:

    public class User {

        private String name;
        private int age;
        private String company;
    }

Now, in order to use YAWP!, just annotate this class with @Endpoint annotation, and add an IdRef&lt;User&gt; field.

    @Endpoint(path = "/users")
    public class User {

        @Id
        private IdRef<User> id;

        /* ... */

    }

Done. Now User is mapped to "/users", and all these urls will be generated:

 * GET /users -> list all users
 * GET /users/:id -> get a user by id
 * POST /users -> create new user with random id
 * POST /users/:id -> create a new user with given id
 * PUT /users/:id -> update user with given id
 * DELETE /users/:id -> deletes user with given id

###IdRef
The IdRef<T> class is a simple wrapper around a long. In YAWP!, every id is a long, and IdRef encapsulates that to make it type safe - that way, you won't be able to assing IdRef&lt;User&gt; to IdRef&lt;Product&gt;, for example.  
It also holds information about the parent id, when such feature is used. Therefore, every id in the system (being primary or foreign key) must be of this type.

### Action
An action is a custom action that can be called over an element or a collection. The default actions already include every REST action, but some entities have business actions associated with then; for example, activate a user, or return the current logged user.

    public class UserActions extends Action<User> {

        @PUT("activate")
        public User activate(IdRef<User> user) {
            return r.save(user.fetch().activate());
        }

        @GET("me", overCollection = true)
        public User me() {
            return Session.getLoggedUser();
        }
    }

Now, the following routes will be created and mapped to your methods:

  * GET /users/me -> call the action me()
  * PUT /users/id/activate -> activate the user on id

Note that, since all ids are long, actions name must start with a letter.

### Transformer
A Transformer can change an object before it is sent on a request. For example, imagine that in some scenarios we don't want to return the User's age in some requests and, in others, we want to calculate his birth year.

    public class UserTransformer extends Transformer<User> {

        public Map<String, String> simple(User user) {
            Map<String, String> result = new HashMap<>();
            result.put("name", user.getName());
            result.put("company", user.getCompany());
            return result;
        }

        public static class UserView {
            private String name;
            private String company;
            private int birthYear;

            public UserView(User user) {
                this.name = user.getName();
                this.company = user.getCompany();
                this.birthYear = Calendar.getInstance().get(Calendar.YEAR) - user.getAge();
            }
        }

        public UserView withYear(User user) {
            return new UserView(user);
        }
    }

To use the transformer, just add a query param to the request:

 * GET /users/id?t=simple -> Returns the user with id applying the transformer simple()
 * GET /users?t=withYear -> Returns the list of all users applying the transformer withYear()

### Hooks
Hooks are codes that are executed before or after a particular action in the system. They can be used to set pre-calculated information in the models to be saved in the database, or to deny access to some users on certain actions, for example. Take a look at this Hook:

    public class UserHook extends Hook<User> {

        @Override
	public void beforeQuery(DatastoreQuery<User> q) {
            q.where("company", "=", Session.getLoggedUser().getCompany());
        }

        @Override
        public void beforeSave(User user) {
            if (user.getAge() < 18) {
                throw new HttpException(422, "You must be 18 or more to sign up.");
            }
        }
    }

This example uses two hook methods. For now, there are only 3 of them:

 * beforeQuery : called before any query made via a URL. It can be used to add security or default validations;
 * beforeSave : called before an object is saved. It can be used to pre-calculate or cache some values in the entity, as well as make validations before saving;
 * afterSave : called after an object is saved. It can be used to trigger actions or log events.

### Querying API
The beforeQuery method recieves a DatastoreQuery<T>. This class is part of the core Query API that YAWP! provides, and it allows for very easy access to the GAE database.  
It can be used within any Repository Feature (i.e., Action, Transformer or Hook). In fact, any of those has a private variable r that represents the Repository. With it, you can the query method to get access to que Query API. See some cool examples:

 * r.query(User.class).where("name", "=", "Mark").and("age", ">=", 21).list();
 * r.query(User.class).where(or(and(c("company", "=", "github.com"), c("age", ">=", 21)), and(c("company", "=", "YAWP!"), c("age", ">=", 18)))).ids();
   The methods c, and and or must be imported static or fully qualified on call for this to work.
 * r.query(User.class).where("name", "=", "John").and("company", "=", "github.com").only();

## Frontend
A frontend api for Javascript is coming soon. Keep tunned for updates!

## Misc
 * http://stackoverflow.com/questions/7160006/m2e-and-having-maven-generated-source-folders-as-eclipse-source-folders
