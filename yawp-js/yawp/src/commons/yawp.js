import { extend } from './utils';

let baseUrl = '/api';
let defaultFetchOptions = {};

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

        let options = {};
        let q = {};

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
                q = {};
            }

            static prepareRequestOptions() {
                let _options = extend({}, options);
                this.clear();
                return _options;
            }

            static baseRequest(type) {
                let options = this.prepareRequestOptions();

                let url = baseUrl + options.url;
                delete options.url;

                options.method = type;
                options.json = true;
                extend(options, defaultFetchOptions);


                return request(url, options);
            }

            static wrapInstance(object) {
                return new this(object);
            }

            static wrapArray(objects) {
                return objects.map((object) => this.wrapInstance(object));
            }

            static wrappedRequest(type) {
                return this.baseRequest(type).then((result) => {
                    if (Array === result.constructor) {
                        return this.wrapArray(result);
                    }
                    return this.wrapInstance(result)
                });
            }

            // query

            static from(parentBaseArg) {
                let parentBase = normalize(parentBaseArg);
                options.url = parentBase + options.url;
                return this;
            }

            static transform(t) {
                this.param('t', t);
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

                let promise = this.wrappedRequest('GET');

                if (cb) {
                    return promise.then(cb);
                }

                return promise;
            }

            static setupQuery() {
                if (Object.keys(q).length > 0) {
                    this.param('q', JSON.stringify(q));
                }
            }

            static list(cb) {
                this.setupQuery();

                let promise = this.wrappedRequest('GET');

                if (cb) {
                    return promise.then(cb);
                }
                return promise;
            }

            static first(cb) {
                this.limit(1);

                return this.list((objects) => {
                    let object = objects.length === 0 ? null : objects[0];
                    return cb ? cb(object) : object;
                });
            }

            static only(cb) {
                return this.list((objects) => {
                    if (objects.length !== 1) {
                        throw 'called only but got ' + objects.length + ' results';
                    }
                    let object = objects[0];
                    return cb ? cb(object) : object;
                });
            }

            // repository

            static create(object) {
                options.body = JSON.stringify(object);
                return this.wrappedRequest('POST');
            }

            static update(object) {
                options.url = object.id;
                options.body = JSON.stringify(object);
                return this.wrappedRequest('PUT');
            }

            static patch(object) {
                options.url = object.id;
                options.body = JSON.stringify(object);
                return this.wrappedRequest('PATCH');
            }

            static destroy(object) {
                if (object) {
                    options.url = extractId(object);
                }
                return this.baseRequest('DELETE');
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
                return this.baseRequest(verb);
            }

            static get(action) {
                return this.action('GET', action);
            }

            static put(action) {
                return this.action('PUT', action);
            }

            static _patch(action) {
                return this.action('PATCH', action);
            }

            static post(action) {
                return this.action('POST', action);
            }

            static _delete(action) {
                return this.action('DELETE', action);
            }

            // es5 subclassing

            static subclass(constructorFn) {
                let base = yawpFn(baseArg);
                let _super = this;
                let sub = class extends base {
                    constructor() {
                        super();
                        _super.bindBaseMethods(this, base);
                        if (constructorFn) {
                            constructorFn.apply(this, arguments);
                        } else {
                            super.constructor.apply(this, arguments);
                        }
                    }
                };
                sub.super = base;
                return sub;
            }

            static bindBaseMethods(self, base) {
                self.super = () => {
                };
                var keys = Object.getOwnPropertyNames(base.prototype);
                for (let i = 0, l = keys.length; i < l; i++) {
                    let key = keys[i];
                    self.super[key] = base.prototype[key].bind(self);
                }
            }

            // instance method

            save(cb) {
                let promise = this.createOrUpdate();
                return cb ? promise.then(cb) : promise;
            }

            createOrUpdate() {
                let promise;
                if (hasId(this)) {
                    options.url = this.id;
                    promise = this.constructor.update(this);
                } else {
                    promise = this.constructor.create(this).then((object) => {
                        this.id = object.id;
                        return object;
                    });
                }
                return promise;
            }

            destroy(cb) {
                let promise = this.constructor.destroy(this);
                return cb ? promise.then(cb) : promise;
            }

            get(action) {
                options.url = extractId(this);
                return this.constructor.get(action);
            }

            put(action) {
                options.url = extractId(this);
                return this.constructor.put(action);
            }

            _patch(action) {
                options.url = extractId(this);
                return this.constructor._patch(action);
            }

            post(action) {
                options.url = extractId(this);
                return this.constructor.post(action);
            }

            _delete(action) {
                options.url = extractId(this);
                return this.constructor._delete(action);
            }
        }

        Yawp.clear();
        return Yawp;
    }

    // base api

    function config(cb) {
        let c = {
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
        let id = extractId(object);
        return yawpFn(id).update(object);
    }

    function patch(object) {
        let id = extractId(object);
        return yawpFn(id).patch(object);
    }

    function destroy(object) {
        let id = extractId(object);
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