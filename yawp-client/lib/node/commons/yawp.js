'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _possibleConstructorReturn2 = require('babel-runtime/helpers/possibleConstructorReturn');

var _possibleConstructorReturn3 = _interopRequireDefault(_possibleConstructorReturn2);

var _inherits2 = require('babel-runtime/helpers/inherits');

var _inherits3 = _interopRequireDefault(_inherits2);

var _get2 = require('babel-runtime/helpers/get');

var _get3 = _interopRequireDefault(_get2);

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

function hasId(object) {
    return object.id;
}

function extractId(object) {
    if (hasId(object)) {
        return object.id;
    }
    throw 'use yawp(id) if your endpoint does not have a @Id field called id';
}

exports.default = function (request) {

    function yawpFn(baseArg) {

        var options = {};
        var q = {};

        var Yawp = function () {
            function Yawp(props) {
                (0, _classCallCheck3.default)(this, Yawp);

                if (props) {
                    (0, _utils.extend)(this, props);
                }
            }

            // request

            (0, _createClass3.default)(Yawp, [{
                key: 'save',


                // instance method

                value: function save(cb) {
                    var promise = this.createOrUpdate();
                    return cb ? promise.then(cb) : promise;
                }
            }, {
                key: 'createOrUpdate',
                value: function createOrUpdate() {
                    var _this = this;

                    var promise = void 0;
                    if (hasId(this)) {
                        options.url = this.id;
                        promise = Yawp.update(this);
                    } else {
                        promise = Yawp.create(this).then(function (object) {
                            _this.id = object.id;
                            return object;
                        });
                    }
                    return promise;
                }
            }, {
                key: 'destroy',
                value: function destroy(cb) {
                    options.url = extractId(this);
                    var promise = Yawp.destroy();
                    return cb ? promise.then(cb) : promise;
                }
            }, {
                key: 'get',
                value: function get(action) {
                    options.url = extractId(this);
                    return Yawp.get(action);
                }
            }, {
                key: 'put',
                value: function put(action) {
                    options.url = extractId(this);
                    return Yawp.put(action);
                }
            }, {
                key: '_patch',
                value: function _patch(action) {
                    options.url = extractId(this);
                    return Yawp._patch(action);
                }
            }, {
                key: 'post',
                value: function post(action) {
                    options.url = extractId(this);
                    return Yawp.post(action);
                }
            }, {
                key: '_delete',
                value: function _delete(action) {
                    options.url = extractId(this);
                    return Yawp._delete(action);
                }
            }], [{
                key: 'clear',
                value: function clear() {
                    options = {
                        url: normalize(baseArg)
                    };
                }
            }, {
                key: 'prepareRequestOptions',
                value: function prepareRequestOptions() {
                    var _options = (0, _utils.extend)({}, options);
                    Yawp.clear();
                    return _options;
                }
            }, {
                key: 'baseRequest',
                value: function baseRequest(type) {
                    var options = Yawp.prepareRequestOptions();

                    var url = _baseUrl + options.url;
                    delete options.url;

                    options.method = type;
                    options.json = true;
                    (0, _utils.extend)(options, _defaultFetchOptions);

                    //console.log('request', url, options);

                    return request(url, options);
                }
            }, {
                key: 'wrapInstance',
                value: function wrapInstance(object) {
                    return new this(object);
                }
            }, {
                key: 'wrapArray',
                value: function wrapArray(objects) {
                    var _this2 = this;

                    return objects.map(function (object) {
                        return _this2.wrapInstance(object);
                    });
                }

                // query

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
                value: function fetch(arg) {
                    var _this3 = this;

                    var cb = typeof arg === 'function' ? arg : undefined;

                    if (arg && !cb) {
                        options.url += '/' + arg;
                    }

                    var promise = Yawp.baseRequest('GET').then(function (object) {
                        return _this3.wrapInstance(object);
                    });

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
                key: 'list',
                value: function list(cb) {
                    var _this4 = this;

                    Yawp.setupQuery();

                    var promise = Yawp.baseRequest('GET').then(function (objects) {
                        return _this4.wrapArray(objects);
                    });

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
                        return cb ? cb(object) : object;
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
                        return cb ? cb(object) : object;
                    });
                }

                // repository

            }, {
                key: 'create',
                value: function create(object) {
                    options.body = JSON.stringify(object);
                    return Yawp.baseRequest('POST');
                }
            }, {
                key: 'update',
                value: function update(object) {
                    options.body = JSON.stringify(object);
                    return Yawp.baseRequest('PUT');
                }
            }, {
                key: 'patch',
                value: function patch(object) {
                    options.body = JSON.stringify(object);
                    return Yawp.baseRequest('PATCH');
                }
            }, {
                key: 'destroy',
                value: function destroy() {
                    return Yawp.baseRequest('DELETE');
                }

                // actions

            }, {
                key: 'json',
                value: function json(object) {
                    options.body = JSON.stringify(object);
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
                key: 'action',
                value: function action(verb, path) {
                    options.url += '/' + path;
                    return Yawp.baseRequest(verb);
                }
            }, {
                key: 'get',
                value: function get(action) {
                    return Yawp.action('GET', action);
                }
            }, {
                key: 'put',
                value: function put(action) {
                    return Yawp.action('PUT', action);
                }
            }, {
                key: '_patch',
                value: function _patch(action) {
                    return Yawp.action('PATCH', action);
                }
            }, {
                key: 'post',
                value: function post(action) {
                    return Yawp.action('POST', action);
                }
            }, {
                key: '_delete',
                value: function _delete(action) {
                    return Yawp.action('DELETE', action);
                }

                // es5 subclassing

            }, {
                key: 'subclass',
                value: function subclass(constructorFn) {
                    var base = yawpFn(baseArg);
                    var sub = function (_base) {
                        (0, _inherits3.default)(sub, _base);

                        function sub() {
                            (0, _classCallCheck3.default)(this, sub);

                            var _this5 = (0, _possibleConstructorReturn3.default)(this, Object.getPrototypeOf(sub).call(this));

                            Yawp.bindBaseMethods(_this5, base);
                            if (constructorFn) {
                                constructorFn.apply(_this5, arguments);
                            } else {
                                (0, _get3.default)(Object.getPrototypeOf(sub.prototype), 'constructor', _this5).apply(_this5, arguments);
                            }
                            return _this5;
                        }

                        return sub;
                    }(base);
                    sub.super = base;
                    return sub;
                }
            }, {
                key: 'bindBaseMethods',
                value: function bindBaseMethods(self, base) {
                    self.super = function () {};
                    var keys = Object.getOwnPropertyNames(base.prototype);
                    for (var i = 0, l = keys.length; i < l; i++) {
                        var key = keys[i];
                        self.super[key] = base.prototype[key].bind(self);
                    }
                }
            }]);
            return Yawp;
        }();

        Yawp.clear();
        return Yawp;
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
//# sourceMappingURL=yawp.js.map