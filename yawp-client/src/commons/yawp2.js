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

    function yawp(baseArg) {

        var options = {
            url: normalize(baseArg)
        };

        var q = {};

        class Yawp {

            constructor(props) {
                extend(this, props);
            }

            // base

            static config(callback) {
                var c = {
                    baseUrl: (url) => {
                        baseUrl = url;
                    },
                    defaultFetchOptions: (options) => {
                        defaultFetchOptions = options;
                    }
                };
                callback(c);
            }

            static baseRequest(type, options) {
                var url = baseUrl + options.url;
                var body = options.data;
                delete options.url;
                delete options.data;

                options.method = type;
                options.body = body;
                options.json = true;
                extend(options, defaultFetchOptions);

                return request(url, options);
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

            static fetch(callback) {
                return Yawp.baseRequest('GET', options).then(callback);
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

            static list(callback) {
                Yawp.setupQuery();
                return baseRequest('GET', options).then(callback);
            }

            static first(callback) {
                Yawp.limit(1);

                return Yawp.list(function (objects) {
                    var object = objects.length === 0 ? null : objects[0];
                    if (callback) {
                        callback(object);
                    }
                });
            }

            static only(callback) {
                return list(function (objects) {
                    if (objects.length !== 1) {
                        throw 'called only but got ' + objects.length + ' results';
                    }
                    if (callback) {
                        callback(objects[0]);
                    }
                });
            }

            // repository

            static create(object) {
                options.data = JSON.stringify(object);
                return Yawp.baseRequest('POST', options);
            }

            static update(object) {
                // TODO: deal with id
                options.data = JSON.stringify(object);
                return Yawp.baseRequest('PUT', options);
            }

            static patch(object) {
                // TODO: deal with id
                options.data = JSON.stringify(object);
                return Yawp.baseRequest('PATCH', options);
            }

            static destroy() {
                // TODO: deal with id
                return Yawp.baseRequest('DELETE', options);
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
                return Yawp.baseRequest('GET', Yawp.actionOptions(action));
            }

            static put(action) {
                return Yawp.baseRequest('PUT', Yawp.actionOptions(action));
            }

            static _patch(action) {
                return Yawp.baseRequest('PATCH', Yawp.actionOptions(action));
            }

            static post(action) {
                return Yawp.baseRequest('POST', Yawp.actionOptions(action));
            }

            static _delete(action) {
                return Yawp.baseRequest('DELETE', Yawp.actionOptions(action));
            }

            // instance method
            save() {
                return Yawp.create(this);
            }
        }

        return Yawp;
    }

    yawp.config = (callback) => {
        var c = {
            baseUrl: (url) => {
                baseUrl = url;
            },
            defaultFetchOptions: (options) => {
                defaultFetchOptions = options;
            }
        };
        callback(c);
    };

    return yawp;
}