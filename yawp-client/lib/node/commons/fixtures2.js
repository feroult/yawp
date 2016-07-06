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

var DEFAULT_BASE_URL = '/fixtures';
var DEFAULT_RESET_URL = '/_ah/yawp/datastore/delete_all';
var DEFAULT_LAZY_PROPERTIES = ['id']; // needed till harmony proxies

exports.default = function (request) {
    var Fixtures = function () {
        function Fixtures() {
            (0, _classCallCheck3.default)(this, Fixtures);

            this.baseUrl = DEFAULT_BASE_URL;
            this.resetUrl = DEFAULT_RESET_URL;
            this.lazyProperties = DEFAULT_LAZY_PROPERTIES;
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
            key: 'reset',
            value: function reset() {
                var _this = this;

                return request(this.resetUrl, {
                    method: 'GET'
                }).then(function () {
                    _this.clear();
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
                this.bindLazy(name, path);
            }
        }, {
            key: 'bindFixture',
            value: function bindFixture(name, path) {
                this[name] = new Fixture(this, name, path).api;
            }
        }, {
            key: 'bindLazy',
            value: function bindLazy(name, path) {
                this.lazy[name] = new Lazy(this, name, path).api;
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
                    callback();
                    return;
                }
                this.promise.then(function () {
                    callback();
                });
            }
        }, {
            key: 'lazyLoadFn',
            value: function lazyLoadFn(name, key) {
                return function () {};
            }
        }, {
            key: 'getLazyDataFor',
            value: function getLazyDataFor(name, key) {
                var lazy = this.lazy[name].self;
                return lazy.getData(key);
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

                return function (key, data) {
                    return _this2.fx.chain(_this2.load(key, data));
                };
            }
        }, {
            key: 'url',
            value: function url() {
                return this.fx.baseUrl + this.path;
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
                    data = this.fx.getLazyDataFor(this.name, key);
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
            key: 'prepare',
            value: function prepare(data) {
                return new Promise(function (resolve) {
                    var object = {};
                    (0, _utils.extend)(object, data);

                    var _iteratorNormalCompletion2 = true;
                    var _didIteratorError2 = false;
                    var _iteratorError2 = undefined;

                    try {
                        var _loop = function _loop() {
                            var key = _step2.value;

                            var value = object[key];
                            if (value instanceof Function) {
                                value().then(function (actualValue) {
                                    object[key] = actualValue;
                                });
                            }
                            // deep into the object
                        };

                        for (var _iterator2 = Object.keys(object)[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
                            _loop();
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

                    return resolve(object);
                });
            }
        }, {
            key: 'createStubs',
            value: function createStubs(key) {
                if (this.hasStubs(key)) {
                    return;
                }
                var self = this;
                this.api[key] = this.fx.lazyProperties.reduce(function (map, name) {
                    map[name] = function () {
                        return new Promise(function (resolve) {
                            return resolve(self.api[key][name]);
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
        function Lazy(fx, name, path) {
            (0, _classCallCheck3.default)(this, Lazy);

            this.fx = fx;
            this.name = name;
            this.path = path;
            this.data = {};
            this.api = this.createApi();
        }

        (0, _createClass3.default)(Lazy, [{
            key: 'createApi',
            value: function createApi() {
                var _this4 = this;

                var api = function api(key, data) {
                    _this4.createLazyStubs(key);
                    _this4.data[key] = data;
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
                var _this5 = this;

                if (this.hasStubs(key)) {
                    return;
                }
                this.api[key] = this.fx.lazyProperties.reduce(function (map, name) {
                    map[name] = _this5.fx.lazyLoadFn(name, key);
                    return map;
                }, {});
                this.api[key].__stub__ = true;
            }
        }, {
            key: 'hasStubs',
            value: function hasStubs(key) {
                return this.api[key] && this.api[key].__stub__;
            }
        }]);
        return Lazy;
    }();

    return new Fixtures();
};

module.exports = exports['default'];
//# sourceMappingURL=fixtures2.js.map