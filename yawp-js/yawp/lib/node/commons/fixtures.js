'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _utils = require('./utils');

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var DEFAULT_BASE_URL = '/fixtures';
var DEFAULT_RESET_URL = '/_ah/yawp/datastore/delete-all';
var DEFAULT_LAZY_PROPERTIES = ['id']; // needed till harmony proxies
var DEFAULT_FETCH_OPTIONS = {};

exports.default = function (request) {
    var Fixtures = function () {
        function Fixtures() {
            _classCallCheck(this, Fixtures);

            this._baseUrl = DEFAULT_BASE_URL;
            this._resetUrl = DEFAULT_RESET_URL;
            this._lazyProperties = DEFAULT_LAZY_PROPERTIES;
            this._defaultFetchOptions = DEFAULT_FETCH_OPTIONS;
            this.promise = null;
            this.fixtures = [];
            this.lazy = {};
        }

        _createClass(Fixtures, [{
            key: 'config',
            value: function config(callback) {
                callback(this);
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
                    return new Promise(function () {
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
            _classCallCheck(this, Fixture);

            this.fx = fx;
            this.name = name;
            this.path = path;
            this.api = this.createApi();
        }

        _createClass(Fixture, [{
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
                        var options = {
                            method: 'POST',
                            json: true,
                            body: JSON.stringify(object)
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

                return new Promise(function (resolve) {
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
                            if ((typeof _ret === 'undefined' ? 'undefined' : _typeof(_ret)) === "object") return _ret.v;
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
                        return new Promise(function (resolve) {
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
            _classCallCheck(this, Lazy);

            this.fx = fx;
            this.name = name;
            this.data = {};
            this.api = this.createApi();
        }

        _createClass(Lazy, [{
            key: 'createApi',
            value: function createApi() {
                var _this6 = this;

                var api = function api(key, data) {
                    _this6.createLazyStubs(key);
                    _this6.data[key] = data;
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