This is the [YAWP! Framework](http://yawp.io) javascript client.

It streamlines the access to your REST APIs from Node.js or a browser.

### Contents

- [Installation](#installation)
- [Setup](#setup)
- [Repository Actions](#repository-actions)
- [Custom Actions](#custom-actions)
- [Query](#query)
- [Transformers](#transformers)
- [Instance Methods](#instance-methods)
- [Class Extension](#class-extension)
- [ES5 Prototypes](#es5-prototypes)

### Installation

__Web__

~~~ html
<script src="https://rawgit.com/feroult/yawp/yawp-1.6.8/yawp-client/lib/web/yawp.min.js"></script>
~~~

__NodeJs__

~~~ bash
npm install yawp --save
~~~

Note: if your environemnt doesn't support ES6 promises, you'll need to use a polyfill like this [one](https://github.com/taylorhakes/promise-polyfill).

### Setup

By default the client routes all API calls to the path __/api__ of the current app's host.
You can override this setting as following:

~~~ javascript
yawp.config(function (c) {
    c.baseUrl('http://your-cors-host.com/api');
});
~~~

### Repository Actions
~~~ javascript
// create
yawp('/people').create({ name: 'janes' }).done(function (person) {
    console.log(person);
});

// update
yawp('/people/1').update({ name: 'janes' }).done(function (person) {
    console.log(person);
});

// patch
yawp('/people/1').patch({ name: 'janes' }).done(function (person) {
    console.log(person);
});

// destroy
yawp('/people/1').destroy().done(function (id) {
    console.log(id);
});

// fetch
yawp('/people/1').fetch(function (person) {
    console.log(person);
});

// list
yawp('/people').list(function (people) {
    console.log(people);
});
~~~

### Custom Actions

~~~ javascript
// @GET("me") over collection action
yawp('/people').get('me').done(function (person) {
    console.log(person);
});

// @PUT("reverse-name") single entity action
yawp('/people/1').put('reverse-name').done(function (person) {
    console.log(person);
});
~~~

### Query

~~~ javascript
// where + list
yawp('/people').where(['name', '=', 'janes']).list(function (people) {
    console.log(people);
});

// where + first
yawp('/people').where(['name', '=', 'janes']).first(function (person) {
    console.log(person);
});

// limit
yawp('/people').where(['name', '=', 'janes']).limit(10).list(function (people) {
    console.log(people);
});

// order
yawp('/people').where(['name', '=', 'janes']).order([{ p: 'name', d: 'asc'}])
               .list(function (people) {
    console.log(people);
});
~~~

### Transfomers

~~~ javascript
// transform + where + list
yawp('/people').transform('upperCase').where(['name', '=', 'janes']).list(function (people) {
    console.log(people);
});

// transform + first
yawp('/people').transform('upperCase').first(function (person) {
    console.log(person);
});
~~~

### Instance Methods

All objects returned by the __yawp query methods__ are wrapped inside an instance of the class __Yawp__.
This class gives us some methods that operate over those instances:

~~~ javascript
yawp('/people/1').fetch(function (person) {
    person.name = 'new name';
    person.save(); // returns a promise
    person.put('active'); // returns a promise
    person._delete('active'); // returns a promise
    person.destroy(); // returns a promise
});
~~~

The complete API methods of the __Yawp__ class can be found [here](https://github.com/feroult/yawp/blob/master/yawp-client/src/commons/yawp.js).

### Class Extension

All yawp client features can be extendend by subclassing the base __Yawp__ class for a given
endpoint.
And this can be done either with the ES6 class syntax or with ES5 prototypes.

For instance, to create a __Person__ class to add and encapsulate some new methods to the
endpoint __/people__, we can do something this:

~~~ javascript
class Person extends yawp('/people') {
}
~~~

Now to add static methods to this endpoint model, we can do:

~~~ javascript
class Person extends yawp('/people') {
    static active() {
        return this.where('status', '=', 'ACTIVE');
    }
}
~~~

Note that now all the objects returned by the API calls using __Person__ will be wrapped inside an
instance of the __Person__ class. With this, it is also possible to add methods that
operate over instances of that class:

~~~ javascript
class Person extends yawp('/people') {
    static inactive() {
        return this.where('status', '=', 'INACTIVE');
    }

    activate() {
        return this.put('active');
    }
}
~~~

And use then in our application code:

~~~ javascript
Person.inative.first(function (person) {
    person.activate().then(function() {
        console.log('person is now active');
    });
})
~~~

Finally, we can override methods:

~~~ javascript
class Person extends yawp('/people') {
    save() {
        console.log('saving...');
        return super.save();
    }
}
~~~

### ES5 Prototypes

If we are running our app in an environment that doesn't support ES6 class syntax,
we have two options. The first is to transpile our ES6 code to ES5 using the [Babel JS](http://babeljs.io).
The other is to use some convenience __YAWP!__ methods. To create the same __Person__ class as above
but in ES5 we can do:

~~~ javascript
var Person = yawp('/people').subclass(/* we can pass a constructor function */);

Person.inactive = function() {
    return this.where('status', '=', 'INACTIVE');
}

Person.prototype.activate = function() {
    return this.put('active');
}
~~~

If we want to override methods, there is a small difference from the ES6 version. With ES5 we
have to access the super methods using the syntax __this.super__, like this:

~~~ javascript
Person.prototype.save = function() {
    console.log('saving...');
    return this.super.save();
}
~~~
