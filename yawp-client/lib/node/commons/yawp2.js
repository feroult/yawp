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

    function yawp(baseArg) {

        var options = {
            url: normalize(baseArg)
        };

        var q = {};

        var Yawp = function () {
            function Yawp(props) {
                (0, _classCallCheck3.default)(this, Yawp);

                (0, _utils.extend)(this, props);
            }

            // base

            (0, _createClass3.default)(Yawp, [{
                key: 'save',


                // instance method
                value: function save() {
                    return Yawp.create(this);
                }
            }], [{
                key: 'config',
                value: function config(callback) {
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
            }, {
                key: 'baseRequest',
                value: function baseRequest(type, options) {
                    var url = _baseUrl + options.url;
                    var body = options.data;
                    delete options.url;
                    delete options.data;

                    options.method = type;
                    options.body = body;
                    options.json = true;
                    (0, _utils.extend)(options, _defaultFetchOptions);

                    return request(url, options);
                }

                // prepare

            }, {
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
                value: function fetch(callback) {
                    return Yawp.baseRequest('GET', options).then(callback);
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
                value: function list(callback) {
                    Yawp.setupQuery();
                    return baseRequest('GET', options).then(callback);
                }
            }, {
                key: 'first',
                value: function first(callback) {
                    Yawp.limit(1);

                    return Yawp.list(function (objects) {
                        var object = objects.length === 0 ? null : objects[0];
                        if (callback) {
                            callback(object);
                        }
                    });
                }
            }, {
                key: 'only',
                value: function only(callback) {
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

            }, {
                key: 'create',
                value: function create(object) {
                    options.data = JSON.stringify(object);
                    return Yawp.baseRequest('POST', options);
                }
            }, {
                key: 'update',
                value: function update(object) {
                    // TODO: deal with id
                    options.data = JSON.stringify(object);
                    return Yawp.baseRequest('PUT', options);
                }
            }, {
                key: 'patch',
                value: function patch(object) {
                    // TODO: deal with id
                    options.data = JSON.stringify(object);
                    return Yawp.baseRequest('PATCH', options);
                }
            }, {
                key: 'destroy',
                value: function destroy() {
                    // TODO: deal with id
                    return Yawp.baseRequest('DELETE', options);
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
                    return Yawp.baseRequest('GET', Yawp.actionOptions(action));
                }
            }, {
                key: 'put',
                value: function put(action) {
                    return Yawp.baseRequest('PUT', Yawp.actionOptions(action));
                }
            }, {
                key: '_patch',
                value: function _patch(action) {
                    return Yawp.baseRequest('PATCH', Yawp.actionOptions(action));
                }
            }, {
                key: 'post',
                value: function post(action) {
                    return Yawp.baseRequest('POST', Yawp.actionOptions(action));
                }
            }, {
                key: '_delete',
                value: function _delete(action) {
                    return Yawp.baseRequest('DELETE', Yawp.actionOptions(action));
                }
            }]);
            return Yawp;
        }();

        return Yawp;
    }

    yawp.config = function (callback) {
        var c = {
            baseUrl: function baseUrl(url) {
                _baseUrl = url;
            },
            defaultFetchOptions: function defaultFetchOptions(options) {
                _defaultFetchOptions = options;
            }
        };
        callback(c);
    };

    return yawp;
};

module.exports = exports['default'];
//# sourceMappingURL=yawp2.js.map