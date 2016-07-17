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

function extractId(object) {
    if (object.id) {
        return object.id;
    }
    throw 'use yawp(id) if your endpoint does not have a @Id field called id';
}

export default (request) => {

    function yawpFn(baseArg) {

        var options = {
            url: normalize(baseArg)
        };

        var q = {};

        class Yawp {

            constructor(props) {
                extend(this, props);
            }

            // prepare

            static from(parentBaseArg) {
                var parentBase = normalize(parentBaseArg);
                options.url = parentBase + options.url;
                return this;
            }

            static transform(t) {
                Yawp.param('t', t);
                return this;
            }

            // query

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

            static fetch(cb) {
                var promise = baseRequest('GET', options);
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

            static url(decode) {
                Yawp.setupQuery();
                var url = baseUrl + options.url + (options.query ? '?' + toUrlParam(options.query) : '');
                if (decode) {
                    return decodeURIComponent(url);
                }
                return url;
            }

            static list(cb) {
                Yawp.setupQuery();
                var promise = baseRequest('GET', options);
                if (cb) {
                    return promise.then(cb);
                }
                return promise;
            }

            static first(cb) {
                Yawp.limit(1);

                return Yawp.list(function (objects) {
                    var object = objects.length === 0 ? null : objects[0];
                    if (cb) {
                        return cb(object);
                    }
                    return object;
                });
            }

            static only(cb) {
                return Yawp.list(function (objects) {
                    if (objects.length !== 1) {
                        throw 'called only but got ' + objects.length + ' results';
                    }
                    var object = objects[0];
                    if (cb) {
                        return cb(object);
                    }
                    return object;
                });
            }

            // repository

            static create(object) {
                options.data = JSON.stringify(object);
                return baseRequest('POST', options);
            }

            static update(object) {
                // TODO: deal with id
                console.log('update', object);
                options.data = JSON.stringify(object);
                return baseRequest('PUT', options);
            }

            static patch(object) {
                // TODO: deal with id
                options.data = JSON.stringify(object);
                return baseRequest('PATCH', options);
            }

            static destroy() {
                // TODO: deal with id
                return baseRequest('DELETE', options);
            }

            // actions

            static actionOptions(action) {
                options.url += '/' + action;
                return options;
            }

            static json(object) {
                options.data = JSON.stringify(object);
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

            static get(action) {
                return baseRequest('GET', Yawp.actionOptions(action));
            }

            static put(action) {
                return baseRequest('PUT', Yawp.actionOptions(action));
            }

            static _patch(action) {
                return baseRequest('PATCH', Yawp.actionOptions(action));
            }

            static post(action) {
                return baseRequest('POST', Yawp.actionOptions(action));
            }

            static _delete(action) {
                return baseRequest('DELETE', Yawp.actionOptions(action));
            }

            // instance method

            save(cb) {
                var promise = Yawp.create(this);
                if (cb) {
                    return promise.then(cb);
                }
                return promise;
            }
        }

        return Yawp;
    }

    // request

    function baseRequest(type, _options) {
        var options = extend({}, _options);

        var url = baseUrl + options.url;
        var body = options.data;
        delete options.url;
        delete options.data;

        options.method = type;
        options.body = body;
        options.json = true;
        extend(options, defaultFetchOptions);

        //console.log('r', url, options);

        return request(url, options);
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