'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _utils = require('./utils');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var _baseUrl = '/api';
var _defaultFetchOptions = {};

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

exports.default = function (request) {

    function yawpFn(baseArg) {

        var options = {
            url: normalize(baseArg)
        };

        var q = {};

        var Yawp = function () {
            function Yawp(props) {
                (0, _classCallCheck3.default)(this, Yawp);

                (0, _utils.extend)(this, props);
            }

            // prepare

            (0, _createClass3.default)(Yawp, [{
                key: 'save',


                // instance method

                value: function save(cb) {
                    var promise = Yawp.create(this);
                    if (cb) {
                        return promise.then(cb);
                    }
                    return promise;
                }
            }], [{
                key: 'from',
                value: function from(parentBaseArg) {
                    var parentBase = normalize(parentBaseArg);
                    options.url = parentBase + options.url;
                    return this;
                }
            }, {
                key: 'transform',
                value: function transform(t) {
                    Yawp.param('t', t);
                    return this;
                }

                // query

            }, {
                key: 'where',
                value: function where(data) {
                    if (arguments.length === 1) {
                        q.where = data;
                    } else {
                        q.where = [].slice.call(arguments);
                    }
                    return this;
                }
            }, {
                key: 'order',
                value: function order(data) {
                    q.order = data;
                    return this;
                }
            }, {
                key: 'sort',
                value: function sort(data) {
                    q.sort = data;
                    return this;
                }
            }, {
                key: 'limit',
                value: function limit(data) {
                    q.limit = data;
                    return this;
                }
            }, {
                key: 'fetch',
                value: function fetch(cb) {
                    var promise = baseRequest('GET', options);
                    if (cb) {
                        return promise.then(cb);
                    }
                    return promise;
                }
            }, {
                key: 'setupQuery',
                value: function setupQuery() {
                    if (Object.keys(q).length > 0) {
                        Yawp.param('q', JSON.stringify(q));
                    }
                }
            }, {
                key: 'url',
                value: function url(decode) {
                    Yawp.setupQuery();
                    var url = _baseUrl + options.url + (options.query ? '?' + toUrlParam(options.query) : '');
                    if (decode) {
                        return decodeURIComponent(url);
                    }
                    return url;
                }
            }, {
                key: 'list',
                value: function list(cb) {
                    Yawp.setupQuery();
                    var promise = baseRequest('GET', options);
                    if (cb) {
                        return promise.then(cb);
                    }
                    return promise;
                }
            }, {
                key: 'first',
                value: function first(cb) {
                    Yawp.limit(1);

                    return Yawp.list(function (objects) {
                        var object = objects.length === 0 ? null : objects[0];
                        if (cb) {
                            return cb(object);
                        }
                        return object;
                    });
                }
            }, {
                key: 'only',
                value: function only(cb) {
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

            }, {
                key: 'create',
                value: function create(object) {
                    options.data = JSON.stringify(object);
                    return baseRequest('POST', options);
                }
            }, {
                key: 'update',
                value: function update(object) {
                    // TODO: deal with id
                    console.log('update', object);
                    options.data = JSON.stringify(object);
                    return baseRequest('PUT', options);
                }
            }, {
                key: 'patch',
                value: function patch(object) {
                    // TODO: deal with id
                    options.data = JSON.stringify(object);
                    return baseRequest('PATCH', options);
                }
            }, {
                key: 'destroy',
                value: function destroy() {
                    // TODO: deal with id
                    return baseRequest('DELETE', options);
                }

                // actions

            }, {
                key: 'actionOptions',
                value: function actionOptions(action) {
                    options.url += '/' + action;
                    return options;
                }
            }, {
                key: 'json',
                value: function json(object) {
                    options.data = JSON.stringify(object);
                    return this;
                }
            }, {
                key: 'params',
                value: function params(_params) {
                    options.query = (0, _utils.extend)(options.query, _params);
                    return this;
                }
            }, {
                key: 'param',
                value: function param(key, value) {
                    if (!options.query) {
                        options.query = {};
                    }
                    options.query[key] = value;
                }
            }, {
                key: 'get',
                value: function get(action) {
                    return baseRequest('GET', Yawp.actionOptions(action));
                }
            }, {
                key: 'put',
                value: function put(action) {
                    return baseRequest('PUT', Yawp.actionOptions(action));
                }
            }, {
                key: '_patch',
                value: function _patch(action) {
                    return baseRequest('PATCH', Yawp.actionOptions(action));
                }
            }, {
                key: 'post',
                value: function post(action) {
                    return baseRequest('POST', Yawp.actionOptions(action));
                }
            }, {
                key: '_delete',
                value: function _delete(action) {
                    return baseRequest('DELETE', Yawp.actionOptions(action));
                }
            }]);
            return Yawp;
        }();

        return Yawp;
    }

    // request

    function baseRequest(type, _options) {
        var options = (0, _utils.extend)({}, _options);

        var url = _baseUrl + options.url;
        var body = options.data;
        delete options.url;
        delete options.data;

        options.method = type;
        options.body = body;
        options.json = true;
        (0, _utils.extend)(options, _defaultFetchOptions);

        //console.log('r', url, options);

        return request(url, options);
    }

    // base api

    function config(cb) {
        var c = {
            baseUrl: function baseUrl(url) {
                _baseUrl = url;
            },
            defaultFetchOptions: function defaultFetchOptions(options) {
                _defaultFetchOptions = options;
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

    var baseApi = {
        config: config,
        update: update,
        patch: patch,
        destroy: destroy
    };

    return (0, _utils.extend)(yawpFn, baseApi);
};

module.exports = exports['default'];
//# sourceMappingURL=yawp2.js.map