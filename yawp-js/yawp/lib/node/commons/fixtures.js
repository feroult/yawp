'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _typeof2 = require('babel-runtime/helpers/typeof');

var _typeof3 = _interopRequireDefault(_typeof2);

var _stringify = require('babel-runtime/core-js/json/stringify');

var _stringify2 = _interopRequireDefault(_stringify);

var _objectWithoutProperties2 = require('babel-runtime/helpers/objectWithoutProperties');

var _objectWithoutProperties3 = _interopRequireDefault(_objectWithoutProperties2);

var _promise = require('babel-runtime/core-js/promise');

var _promise2 = _interopRequireDefault(_promise);

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _utils = require('./utils');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var DEFAULT_BASE_URL = '/fixtures';
var DEFAULT_RESET_URL = '/_ah/yawp/datastore/delete-all';
var DEFAULT_LAZY_PROPERTIES = ['id']; // needed till harmony proxies
var DEFAULT_FETCH_OPTIONS = {};

exports.default = function (request) {
    var Fixtures = function () {
        function Fixtures() {
            (0, _classCallCheck3.default)(this, Fixtures);

            this._baseUrl = DEFAULT_BASE_URL;
            this._resetUrl = DEFAULT_RESET_URL;
            this._lazyProperties = DEFAULT_LAZY_PROPERTIES;
            this._defaultFetchOptions = DEFAULT_FETCH_OPTIONS;
            this._defaultNamespace = null;
            this.promise = null;
            this.fixtures = [];
            this.lazy = {};
        }

        (0, _createClass3.default)(Fixtures, [{
            key: 'config',
            value: function config(callback) {
                callback(this);
            }
        }, {
            key: 'defaultNamespace',
            value: function defaultNamespace(ns) {
                this._defaultNamespace = ns;
            }
        }, {
            key: 'baseUrl',
            value: function baseUrl(url) {
                this._baseUrl = url;
            }
        }, {
            key: 'resetUrl',
            value: function resetUrl(url) {
                this._resetUrl = url;
            }
        }, {
            key: 'lazyProperties',
            value: function lazyProperties(properties) {
                this._lazyProperties = properties;
            }
        }, {
            key: 'defaultFetchOptions',
            value: function defaultFetchOptions(options) {
                (0, _utils.extend)(this._defaultFetchOptions, options);
            }
        }, {
            key: 'reset',
            value: function reset(all) {
                var _this = this;

                return request(this._resetUrl, {
                    method: 'GET'
                }).then(function () {
                    _this.clear(all);
                });
            }
        }, {
            key: 'clear',
            value: function clear(all) {
                this.promise = null;
                for (var i = 0, l = this.fixtures.length; i < l; i++) {
                    var name = this.fixtures[i].name;
                    var path = this.fixtures[i].path;
                    this.bindFixture(name, path);
                    all && this.bindLazy(name, path);
                }
            }
        }, {
            key: 'bind',
            value: function bind(name, path) {
                this.fixtures.push({ name: name, path: path });
                this.bindFixture(name, path);
                this.bindLazy(name);
            }
        }, {
            key: 'bindFixture',
            value: function bindFixture(name, path) {
                this[name] = new Fixture(this, name, path).api;
            }
        }, {
            key: 'bindLazy',
            value: function bindLazy(name) {
                this.lazy[name] = new Lazy(this, name).api;
            }
        }, {
            key: 'chain',
            value: function chain(promiseFn) {
                if (!this.promise) {
                    this.promise = promiseFn();
                } else {
                    this.promise = this.promise.then(promiseFn);
                }
                return this.promise;
            }
        }, {
            key: 'load',
            value: function load(callback) {
                if (!this.promise) {
                    return new _promise2.default(function () {
                        return callback && callback();
                    });
                }
                return this.promise.then(function () {
                    return callback && callback();
                });
            }
        }]);
        return Fixtures;
    }();

    var Fixture = function () {
        function Fixture(fx, name, path) {
            (0, _classCallCheck3.default)(this, Fixture);

            this.fx = fx;
            this.name = name;
            this.path = path;
            this.api = this.createApi();
        }

        (0, _createClass3.default)(Fixture, [{
            key: 'createApi',
            value: function createApi() {
                var _this2 = this;

                var api = function api(key, data) {
                    return _this2.fx.chain(_this2.load(key, data));
                };
                api.self = this;
                return api;
            }
        }, {
            key: 'url',
            value: function url() {
                return this.fx._baseUrl + this.path;
            }
        }, {
            key: 'load',
            value: function load(key, data) {
                this.createStubs(key);
                return this.createLoadPromiseFn(key, data);
            }
        }, {
            key: 'createLoadPromiseFn',
            value: function createLoadPromiseFn(key, data) {
                var _this3 = this;

                if (!data) {
                    data = this.getLazyDataFor(key);
                }

                return function () {
                    if (_this3.isLoaded(key)) {
                        return _this3.api[key];
                    }

                    return _this3.prepare(data).then(function (object) {
                        var __namespace = object.__namespace,
                            data = (0, _objectWithoutProperties3.default)(object, ['__namespace']);

                        delete object.__namespace;
                        var namespace = __namespace === undefined ? _this3.fx._defaultNamespace : __namespace;
                        var options = {
                            method: 'POST',
                            headers: { namespace: namespace },
                            json: true,
                            body: (0, _stringify2.default)(data)
                        };

                        (0, _utils.extend)(options, _this3.fx._defaultFetchOptions);

                        return request(_this3.url(), options).then(function (response) {
                            _this3.api[key] = response;
                            return response;
                        });
                    });
                };
            }
        }, {
            key: 'getLazyDataFor',
            value: function getLazyDataFor(key) {
                var lazy = this.fx.lazy[this.name].self;
                return lazy.getData(key);
            }
        }, {
            key: 'prepare',
            value: function prepare(data) {
                var _this4 = this;

                return new _promise2.default(function (resolve) {
                    var object = {};
                    (0, _utils.extend)(object, data);

                    var lazyProperties = [];
                    _this4.inspectLazyProperties(object, lazyProperties);
                    _this4.resolveLazyProperties(object, lazyProperties, resolve);
                });
            }
        }, {
            key: 'resolveLazyProperties',
            value: function resolveLazyProperties(object, lazyProperties, resolve) {
                if (!lazyProperties.length) {
                    resolve(object);
                } else {
                    var promise = lazyProperties[0]();
                    for (var i = 1, l = lazyProperties.length; i < l; i++) {
                        promise = promise.then(lazyProperties[i]);
                    }

                    promise.then(function () {
                        resolve(object);
                    });
                }
            }
        }, {
            key: 'inspectLazyProperties',
            value: function inspectLazyProperties(object, lazyProperties) {
                var _this5 = this;

                var _loop = function _loop(key) {
                    if (!object.hasOwnProperty(key)) {
                        return 'continue';
                    }

                    var value = object[key];
                    if (value instanceof Function) {
                        lazyProperties.push(function () {
                            return value().then(function (actualValue) {
                                object[key] = actualValue;
                            });
                        });
                        return 'continue';
                    }
                    if (value instanceof Object) {
                        _this5.inspectLazyProperties(value, lazyProperties);
                        return {
                            v: void 0
                        };
                    }
                };

                for (var key in object) {
                    var _ret = _loop(key);

                    switch (_ret) {
                        case 'continue':
                            continue;

                        default:
                            if ((typeof _ret === 'undefined' ? 'undefined' : (0, _typeof3.default)(_ret)) === "object") return _ret.v;
                    }
                }
            }
        }, {
            key: 'createStubs',
            value: function createStubs(key) {
                if (this.hasStubs(key)) {
                    return;
                }
                var self = this;
                this.api[key] = this.fx._lazyProperties.reduce(function (map, property) {
                    map[property] = function () {
                        return new _promise2.default(function (resolve) {
                            return resolve(self.api[key][property]);
                        });
                    };
                    return map;
                }, {});
                this.api[key].__stub__ = true;
            }
        }, {
            key: 'isLoaded',
            value: function isLoaded(key) {
                return this.api[key] && !this.hasStubs(key);
            }
        }, {
            key: 'hasStubs',
            value: function hasStubs(key) {
                return this.api[key] && this.api[key].__stub__;
            }
        }]);
        return Fixture;
    }();

    var Lazy = function () {
        function Lazy(fx, name) {
            (0, _classCallCheck3.default)(this, Lazy);

            this.fx = fx;
            this.name = name;
            this.data = {};
            this.api = this.createApi();
        }

        (0, _createClass3.default)(Lazy, [{
            key: 'createApi',
            value: function createApi() {
                var _this6 = this;

                var api = function api(key, data) {
                    _this6.createLazyStubs(key);
                    if (data) {
                        _this6.data[key] = data;
                    }
                    return _this6.api[key];
                };
                api.self = this;
                return api;
            }
        }, {
            key: 'getData',
            value: function getData(key) {
                return this.data[key];
            }
        }, {
            key: 'createLazyStubs',
            value: function createLazyStubs(key) {
                var _this7 = this;

                if (this.hasStubs(key)) {
                    return;
                }
                this.api[key] = this.fx._lazyProperties.reduce(function (map, property) {
                    map[property] = function () {
                        return _this7.getFixtureRef().load(key)().then(function (object) {
                            return object[property];
                        });
                    };
                    return map;
                }, {});
                this.api[key].__stub__ = true;
            }
        }, {
            key: 'hasStubs',
            value: function hasStubs(key) {
                return this.api[key] && this.api[key].__stub__;
            }
        }, {
            key: 'getFixtureRef',
            value: function getFixtureRef() {
                return this.fx[this.name].self;
            }
        }]);
        return Lazy;
    }();

    return new Fixtures();
};

module.exports = exports['default'];
//# sourceMappingURL=fixtures.js.map