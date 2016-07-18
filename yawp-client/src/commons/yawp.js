import { extend } from './utils';

var baseUrl = '/api';
var defaultFetchOptions = {};

function normalize(arg) {
    if (!arg) {
        return '';
    }
    if (arg instanceof Object) {
        return extractId(arg);
    }
    return arg;
}

function hasId(object) {
    return object.id;
}

function extractId(object) {
    if (hasId(object)) {
        return object.id;
    }
    throw 'use yawp(id) if your endpoint does not have a @Id field called id';
}

export default (request) => {

    function yawpFn(baseArg) {

        var options = {};
        var q = {};

        class Yawp {

            constructor(props) {
                if (props) {
                    extend(this, props);
                }
            }

            // request

            static clear() {
                options = {
                    url: normalize(baseArg)
                };
            }

            static prepareRequestOptions() {
                var _options = extend({}, options);
                Yawp.clear();
                return _options;
            }

            static baseRequest(type) {
                var options = Yawp.prepareRequestOptions();

                var url = baseUrl + options.url;
                delete options.url;

                options.method = type;
                options.json = true;
                extend(options, defaultFetchOptions);

                //console.log('r', url, options);

                return request(url, options);
            }

            static wrapInstance(object) {
                return new this(object);
            }

            static wrapArray(objects) {
                return objects.map((object) => this.wrapInstance(object));
            }

            // query

            static from(parentBaseArg) {
                var parentBase = normalize(parentBaseArg);
                options.url = parentBase + options.url;
                return this;
            }

            static transform(t) {
                Yawp.param('t', t);
                return this;
            }

            static where(data) {
                if (arguments.length === 1) {
                    q.where = data;
                } else {
                    q.where = [].slice.call(arguments);
                }
                return this;
            }

            static order(data) {
                q.order = data;
                return this;
            }

            static sort(data) {
                q.sort = data;
                return this;
            }

            static limit(data) {
                q.limit = data;
                return this;
            }

            static fetch(arg) {
                let cb = typeof arg === 'function' ? arg : undefined;

                if (arg && !cb) {
                    options.url += '/' + arg;
                }

                var promise = Yawp.baseRequest('GET').then((object) => {
                    return this.wrapInstance(object);
                });

                if (cb) {
                    return promise.then(cb);
                }

                return promise;
            }

            static setupQuery() {
                if (Object.keys(q).length > 0) {
                    Yawp.param('q', JSON.stringify(q));
                }
            }

            static list(cb) {
                Yawp.setupQuery();

                var promise = Yawp.baseRequest('GET').then((objects) => {
                    return this.wrapArray(objects);
                });

                if (cb) {
                    return promise.then(cb);
                }
                return promise;
            }

            static first(cb) {
                Yawp.limit(1);

                return Yawp.list((objects) => {
                    var object = objects.length === 0 ? null : objects[0];
                    return cb ? cb(object) : object;
                });
            }

            static only(cb) {
                return Yawp.list((objects) => {
                    if (objects.length !== 1) {
                        throw 'called only but got ' + objects.length + ' results';
                    }
                    var object = objects[0];
                    return cb ? cb(object) : object;
                });
            }

            // repository

            static create(object) {
                options.body = JSON.stringify(object);
                return Yawp.baseRequest('POST');
            }

            static update(object) {
                options.body = JSON.stringify(object);
                return Yawp.baseRequest('PUT');
            }

            static patch(object) {
                options.body = JSON.stringify(object);
                return Yawp.baseRequest('PATCH');
            }

            static destroy() {
                return Yawp.baseRequest('DELETE');
            }

            // actions

            static json(object) {
                options.body = JSON.stringify(object);
                return this;
            }

            static params(params) {
                options.query = extend(options.query, params);
                return this;
            }

            static param(key, value) {
                if (!options.query) {
                    options.query = {};
                }
                options.query[key] = value;
            }

            static action(verb, path) {
                options.url += '/' + path;
                return Yawp.baseRequest(verb);
            }

            static get(action) {
                return Yawp.action('GET', action);
            }

            static put(action) {
                return Yawp.action('PUT', action);
            }

            static _patch(action) {
                return Yawp.action('PATCH', action);
            }

            static post(action) {
                return Yawp.action('POST', action);
            }

            static _delete(action) {
                return Yawp.action('DELETE', action);
            }

            // es5 subclassing

            static subclass(constructorFn) {
                return class extends yawpFn(baseArg) {
                    constructor() {
                        super();
                        if (constructorFn) {
                            constructorFn.apply(this, arguments);
                        } else {
                            super.constructor.apply(this, arguments);
                        }
                    }

                    superConstructor() {
                        super.constructor.apply(this, arguments);
                    }
                };
            }

            // instance method

            save(cb) {
                var promise = this.createOrUpdate();
                return cb ? promise.then(cb) : promise;
            }

            createOrUpdate() {
                var promise;
                if (hasId(this)) {
                    options.url = this.id;
                    promise = Yawp.update(this);
                } else {
                    promise = Yawp.create(this).then((object) => {
                        this.id = object.id;
                        return object;
                    });
                }
                return promise;
            }

            destroy(cb) {
                options.url = extractId(this);
                var promise = Yawp.destroy();
                return cb ? promise.then(cb) : promise;
            }

            get(action) {
                options.url = extractId(this);
                return Yawp.get(action);
            }

            put(action) {
                options.url = extractId(this);
                return Yawp.put(action);
            }

            _patch(action) {
                options.url = extractId(this);
                return Yawp._patch(action);
            }

            post(action) {
                options.url = extractId(this);
                return Yawp.post(action);
            }

            _delete(action) {
                options.url = extractId(this);
                return Yawp._delete(action);
            }
        }

        Yawp.clear();
        return Yawp;
    }

    // base api

    function config(cb) {
        var c = {
            baseUrl: (url) => {
                baseUrl = url;
            },
            defaultFetchOptions: (options) => {
                defaultFetchOptions = options;
            }
        };
        cb(c);
    };

    function update(object) {
        var id = extractId(object);
        return yawpFn(id).update(object);
    }

    function patch(object) {
        var id = extractId(object);
        return yawpFn(id).patch(object);
    }

    function destroy(object) {
        var id = extractId(object);
        return yawpFn(id).destroy(object);
    }

    let baseApi = {
        config,
        update,
        patch,
        destroy
    }

    return extend(yawpFn, baseApi);
}