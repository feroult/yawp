'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

exports.default = function (request) {

    function config(callback) {
        var c = {
            baseUrl: function baseUrl(url) {
                _baseUrl = url;
            },
            defaultFetchOptions: function defaultFetchOptions(options) {
                _defaultFetchOptions = options;
            }
        };

        callback(c);
    }

    function baseRequest(type, options) {
        var url = _baseUrl + options.url;
        var body = options.data;
        delete options.url;

        options.method = type;
        options.body = body;
        options.json = true;
        (0, _utils.extend)(options, _defaultFetchOptions);

        return request(url, options);
    }

    function extractId(object) {
        if (object.id) {
            return object.id;
        }
        throw 'use yawp(id) if your endpoint does not have a @Id field called id';
    }

    function query(options) {
        var q = {};

        function where(data) {
            if (arguments.length === 1) {
                q.where = data;
            } else {
                q.where = [].slice.call(arguments);
            }
            return this;
        }

        function order(data) {
            q.order = data;
            return this;
        }

        function sort(data) {
            q.sort = data;
            return this;
        }

        function limit(data) {
            q.limit = data;
            return this;
        }

        function fetch(callback) {
            return baseRequest('GET', options()).then(callback);
        }

        function setupQuery() {
            if (Object.keys(q).length > 0) {
                options.addQueryParameter('q', JSON.stringify(q));
            }
        }

        function url(decode) {
            setupQuery();
            var url = _baseUrl + options().url + (options().query ? '?' + toUrlParam(options().query) : '');
            if (decode) {
                return decodeURIComponent(url);
            }
            return url;
        }

        function list(callback) {
            setupQuery();
            return baseRequest('GET', options()).then(callback);
        }

        function first(callback) {
            limit(1);

            return list(function (objects) {
                var object = objects.length === 0 ? null : objects[0];
                if (callback) {
                    callback(object);
                }
            });
        }

        function only(callback) {
            return list(function (objects) {
                if (objects.length !== 1) {
                    throw 'called only but got ' + objects.length + ' results';
                }
                if (callback) {
                    callback(objects[0]);
                }
            });
        }

        return {
            where: where,
            order: order,
            sort: sort,
            limit: limit,
            fetch: fetch,
            list: list,
            first: first,
            only: only,
            url: url
        };
    }

    function repository(options) {
        function create(object) {
            options().data = JSON.stringify(object);
            return baseRequest('POST', options());
        }

        function update(object) {
            options().data = JSON.stringify(object);
            return baseRequest('PUT', options());
        }

        function patch(object) {
            options().data = JSON.stringify(object);
            return baseRequest('PATCH', options());
        }

        function destroy() {
            return baseRequest('DELETE', options());
        }

        return {
            create: create,
            update: update,
            patch: patch,
            destroy: destroy
        };
    }

    function actions(options) {
        function actionOptions(action) {
            options().url += '/' + action;
            return options();
        }

        function json(object) {
            options.setJson(object);
            return this;
        }

        function params(params) {
            options.addQueryParameters(params);
            return this;
        }

        function get(action) {
            return baseRequest('GET', actionOptions(action));
        }

        function put(action) {
            return baseRequest('PUT', actionOptions(action));
        }

        function _patch(action) {
            return baseRequest('PATCH', actionOptions(action));
        }

        function post(action) {
            return baseRequest('POST', actionOptions(action));
        }

        function _delete(action) {
            return baseRequest('DELETE', actionOptions(action));
        }

        return {
            json: json,
            params: params,
            get: get,
            put: put,
            _patch: _patch,
            post: post,
            _delete: _delete
        };
    }

    function yawp(baseArg) {
        function normalize(arg) {
            if (!arg) {
                return '';
            }
            if (arg instanceof Object) {
                return extractId(arg);
            }
            return arg;
        }

        var ajaxOptions = {
            url: normalize(baseArg),
            async: true
        };

        function options() {
            return ajaxOptions;
        }

        options.setJson = function (object) {
            ajaxOptions.data = JSON.stringify(object);
        };

        options.addQueryParameters = function (params) {
            ajaxOptions.query = (0, _utils.extend)(ajaxOptions.query, params);
        };

        options.addQueryParameter = function (key, value) {
            if (!ajaxOptions.query) {
                ajaxOptions.query = {};
            }
            ajaxOptions.query[key] = value;
        };

        function from(parentBaseArg) {
            var parentBase = normalize(parentBaseArg);
            options().url = parentBase + options().url;
            return this;
        }

        function transform(t) {
            options.addQueryParameter('t', t);
            return this;
        }

        function sync() {
            ajaxOptions.async = false;
            return this;
        }

        function f() {}
        h;
        return (0, _utils.extend)(f, {
            from: from,
            transform: transform,
            sync: sync
        }, query(options), repository(options), actions(options));
    }

    function update(object) {
        var id = extractId(object);
        return yawp(id).update(object);
    }

    function patch(object) {
        var id = extractId(object);
        return yawp(id).patch(object);
    }

    function destroy(object) {
        var id = extractId(object);
        return yawp(id).destroy(object);
    }

    var api = {
        config: config,
        update: update,
        patch: patch,
        destroy: destroy
    };

    return (0, _utils.extend)(yawp, api);
};

var _utils = require('./utils');

var _baseUrl = '/api';
var _defaultFetchOptions = {};

module.exports = exports['default'];
//# sourceMappingURL=yawp.js.map