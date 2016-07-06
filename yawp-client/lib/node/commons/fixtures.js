'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _typeof2 = require('babel-runtime/helpers/typeof');

var _typeof3 = _interopRequireDefault(_typeof2);

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _utils = require('./utils');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var DEFAULT_BASE_URL = '/fixtures';
var DEFAULT_RESET_URL = '/_ah/yawp/datastore/delete_all';
var DEFAULT_LAZY_PROPERTIES = ['id']; // needed till harmony proxies

exports.default = function (request) {
    var Fixtures = function () {
        function Fixtures() {
            (0, _classCallCheck3.default)(this, Fixtures);

            this._baseUrl = DEFAULT_BASE_URL;
            this._resetUrl = DEFAULT_RESET_URL;
            this._lazyProperties = DEFAULT_LAZY_PROPERTIES;
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
                var _iteratorNormalCompletion = true;
                var _didIteratorError = false;
                var _iteratorError = undefined;

                try {
                    for (var _iterator = this.fixtures[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
                        var _step$value = _step.value;
                        var name = _step$value.name;
                        var path = _step$value.path;

                        this.bindFixture(name, path);
                        all && this.bindLazy(name, path);
                    }
                } catch (err) {
                    _didIteratorError = true;
                    _iteratorError = err;
                } finally {
                    try {
                        if (!_iteratorNormalCompletion && _iterator.return) {
                            _iterator.return();
                        }
                    } finally {
                        if (_didIteratorError) {
                            throw _iteratorError;
                        }
                    }
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
                        return request(_this3.url(), {
                            method: 'POST',
                            json: true,
                            body: JSON.stringify(object)
                        }).then(function (response) {
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

                var _iteratorNormalCompletion2 = true;
                var _didIteratorError2 = false;
                var _iteratorError2 = undefined;

                try {
                    var _loop = function _loop() {
                        var key = _step2.value;

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

                    for (var _iterator2 = Object.keys(object)[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
                        var _ret = _loop();

                        switch (_ret) {
                            case 'continue':
                                continue;

                            default:
                                if ((typeof _ret === 'undefined' ? 'undefined' : (0, _typeof3.default)(_ret)) === "object") return _ret.v;
                        }
                    }
                } catch (err) {
                    _didIteratorError2 = true;
                    _iteratorError2 = err;
                } finally {
                    try {
                        if (!_iteratorNormalCompletion2 && _iterator2.return) {
                            _iterator2.return();
                        }
                    } finally {
                        if (_didIteratorError2) {
                            throw _iteratorError2;
                        }
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