# YAWP!

Yet Another Web API for Google App Engine

[![Build Status](https://api.shippable.com/projects/5418400b50f3833e055ab249/badge?branchName=master)](https://app.shippable.com/projects/5418400b50f3833e055ab249/builds/latest)

## Introduction

**YAWP!** is a Java framework built on top of Google App Engine whose main purpose is help developers to create a simple and meaningful API to support their REST based applications.

From a single class annotation, it provides a full REST url schema with a fluent progamatic API for Java and Javascript. You write your client side code the same way you do for your server side. It also provides a convenient way to organize your server side business logic.

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

To define the identity field of a domain object you need to declare an IdRef&lt;T&gt; annotated with @Id. Then you can use this identity as a reference from another domain object.

Also, through the @ParentId annotation, IdRef exposes the Ancestor mechanism of the Datastore Key architecuture, leveraging it's strong consistency model when necessary:

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

### Query API

From a HTTP call, a Java or Javascript method you can query your objects using a fluent API that nicely exposes the  Datastore Query class.

**Javascript**:
```javascript
yawp('/people').where([ 'name', '=', 'Janes']).first( function(person) {} );

yawp('/people').order([ { p: 'name', d: 'desc' } ]).list( function(people) {} ); 

yawp('/people').from(parentId).list( function(people) {} ); 
````

**Java**:
```java
Person person = yawp(Person.class).where("name", "=", "Janes").first();

List<Person> people = yawp(Person.class).order("name", "desc").list(); 

List<Person> people = yawp(Person.class).from(parentId).list(); 
```

Other Java examples, also avaibale from HTTP or Javascript:

```java
yawp(User.class).where("name", "=", "Mark").and("age", ">=", 21).list();
yawp(User.class).where(or(and(c("company", "=", "github.com"), c("age", ">=", 21)), and(c("company", "=", "YAWP!"), c("age", ">=", 18)))).ids();
yawp(User.class).where("name", "=", "John").and("company", "=", "github.com").only();
```
Note: The methods **c**, **and** and **or** must be imported static or fully qualified for this to work.


You can look at this [Java test suite](../master/src/test/java/io/yawp/repository/query/DatastoreQueryTest.java) to see examples of more complex constructions.

### Endpoint Features

So far, you've seen all functionality that you get by just annotating your POJO with @Endpoint. Now it's time to see how to add custom server side business logic to your model, so you can create real world applications with specific needs.

The way **YAWP!** deal with this is by allowing you to extend the default REST schema through **Features**. You can create three kind of features for your objects: **Actions**, **Transformers** and **Hooks**. 

### Actions

To add custom behavior to your domain object you can use the Action API. Imagine you need to activate a given person. To do this you can create an Action class:

```java
public class ActivatePersonAction extends Action<Person> {

    @PUT("active")
    public void activate(IdRef<Person> id) {
        Person person = id.fetch();
        person.setActive(true);
        yawp.save(person);
    }
    
}
```

Now, to activate a given person, let's say, with id 123, you can:

<pre>
curl -X <b>PUT</b> http://localhost:8080/api<b>/people/123/active</b>
</pre>

The **Javascript** equivalent would be:
```javascript
yawp('/people/123').put('active').done( function(status) {} );
```

Also, an action be called over a single domain object or over a collection. For an action over a collection, don't specify it's IdRef or specify it's parent IdRef as the first argument:

```java
    public class UserActions extends Action<User> {

 	// over collection without IdRef
        @GET("me")
        public User me() {
            return Session.getLoggedUser();
        }
        
        // over collection with parent IdRef
        @GET("first")
        public User firstUser(IdRef<Company> companyId) {
            return yawp(User.class).where("companyId", "=", companyId).first();
        }
    }
```

The following routes will be created and mapped to your methods:

  * GET /users/me -> call the action me()
  * GET /company/{companyId}/users/firstChild -> call the action firstUser()

### Transformers

The Transformer API is used to create different views of the same domain object. If you wan't to add or hide information to be returned to the client, the way to go is to use a Transfomer. For instance:

```java
public class BasicObjectTransformer extends Transformer<Person> {

    public Map<String, Object> upperCase(Person person) {
    
        Map<String, Object> map = asMap(person);
        map.put("name", person.getName().toUpperCase());
        return map;
	
    }

}
```

Now, to transform a given person, let's say, with id 123, you can:

<pre>
curl -X <b>GET</b> http://localhost:8080/api<b>/people/123?t=upperCase</b>
</pre>

The **Javascript** equivalent would be:
```javascript
yawp('/people/123').transform('upperCase').first( function(person) {} );
```
Note: All transformers can be applied for collections queries or feching single objects.

### Hooks

Hooks are portions of business logic that are executed before or after a particular action in the system. They can be used to set pre-calculated information on a domain object or to deny access to some users on certain actions. For example, take a look at this Hook:

```java
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
```java

You can define 3 Hook types for your application:

 * beforeQuery : called before any query made via a URL. It can be used to add security or default validations;
 * beforeSave : called before an object is saved. It can be used to pre-calculate or cache some values in the entity, as well as make validations before saving;
 * afterSave : called after an object is saved. It can be used to trigger actions or log events.


### Benefits

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


## Frontend
A frontend api for Javascript is coming soon. Keep tunned for updates!

## Misc
 * http://stackoverflow.com/questions/7160006/m2e-and-having-maven-generated-source-folders-as-eclipse-source-folders
