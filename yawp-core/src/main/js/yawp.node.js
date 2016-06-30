module.exports = (function () {
    var request = require('request');

    var baseUrl = '/api';

    function config(callback) {
        var c = {
            baseUrl: function (url) {
                baseUrl = url;
            }
        };

        callback(c);
    }

    function extend() {
        var result = arguments[0] || {};

        for (var i = 1, l = arguments.length; i < l; i++) {
            var obj = arguments[i];
            for (var attrname in obj) {
                result[attrname] = obj[attrname];
            }
        }

        return result;
    }

    function defaultAjax(type, options) {
        var requestOptions = {
            headers: {
                'charset': 'UTF-8'
            },
            enconding: 'utf8',
            method: type,
            url: baseUrl + options.url,
            qs: options.query,
            json: true,
            gzip: true,
            body: options.data
        };

        var fail, done;

        request(requestOptions, function (error, response, body) {
            if (!error && response.statusCode == 200) {

                if (done) {
                    done(body);
                }
                return;
            }
            if (fail) {
                fail(response);
            }
        });

        var callbacks = {
            fail: function (callback) {
                fail = callback;
                return callbacks;
            },
            done: function (callback) {
                done = callback;
                return callbacks;
            }
        }

        return callbacks;
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
            q.where = data;
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
            return defaultAjax('GET', options()).done(callback);
        }

        function list(callback) {
            if (Object.keys(q).length > 0) {
                options.addQueryParameter('q', JSON.stringify(q));
            }
            return defaultAjax('GET', options()).done(callback);
        }

        function first(callback) {
            limit(1);

            return list(function (objects) {
                var object = objects.length == 0 ? null : objects[0];
                if (callback) {
                    callback(object);
                }
            });
        }

        function only(callback) {
            return list(function (objects) {
                if (objects.length != 1) {
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
            only: only
        };
    }

    function repository(options) {
        function create(object) {
            options().data = object;
            return defaultAjax('POST', options());
        }

        function update(object) {
            options().data = object;
            return defaultAjax('PUT', options());
        }

        function destroy() {
            return defaultAjax('DELETE', options());
        }

        return {
            create: create,
            update: update,
            destroy: destroy
        };
    }

    function actions(options) {
        function actionOptions(action) {
            options().url += '/' + action;
            return options();
        }

        function params(params) {
            options.addQueryParameters(params);
            return this;
        }

        function get(action) {
            return defaultAjax('GET', actionOptions(action));
        }

        function put(action) {
            return defaultAjax('PUT', actionOptions(action));
        }

        function post(action) {
            return defaultAjax('POST', actionOptions(action));
        }

        function _delete(action) {
            return defaultAjax('DELETE', actionOptions(action));
        }

        return {
            params: params,
            get: get,
            put: put,
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
            url: normalize(baseArg)
        }

        function options() {
            return ajaxOptions;
        }

        options.addQueryParameters = function (params) {
            ajaxOptions.query = extend(ajaxOptions.query, params);
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

        return extend({
            from: from,
            transform: transform,
        }, query(options), repository(options), actions(options));
    }

    function update(object) {
        var id = extractId(object);
        return yawp(id).update(object);
    }

    function destroy(object) {
        var id = extractId(object);
        return yawp(id).destroy(object);
    }

    var api = {
        config: config,
        update: update,
        destroy: destroy
    };

    return extend(yawp, api);

})();