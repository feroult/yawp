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
            this.init();
        }

        (0, _createClass3.default)(Fixtures, [{
            key: 'init',
            value: function init() {
                this.promise = null;
            }
        }, {
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
                    _this.init();
                });
            }
        }, {
            key: 'bind',
            value: function bind(name, path) {
                this[name] = new EndpointFixture(this, name, path).api;
            }
        }, {
            key: 'chain',
            value: function chain(promiseFn) {
                if (!this.promise) {
                    this.promise = promiseFn();
                    return this.promise;
                }
                return this.promise.then(promiseFn);
            }
        }]);
        return Fixtures;
    }();

    var EndpointFixture = function () {
        function EndpointFixture(fx, name, path) {
            (0, _classCallCheck3.default)(this, EndpointFixture);

            this.fx = fx;
            this.name = name;
            this.path = path;
            this.api = this.createApi();
        }

        (0, _createClass3.default)(EndpointFixture, [{
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
                this.createLazyPropertyLoader(key);
                return this.createLoadPromiseFn(key, data);
            }
        }, {
            key: 'createLoadPromiseFn',
            value: function createLoadPromiseFn(key, data) {
                var _this3 = this;

                return function () {
                    return request(_this3.url(), {
                        method: 'POST',
                        json: true,
                        body: JSON.stringify(_this3.prepare(data))
                    }).then(function (response) {
                        _this3.api[key] = response;
                        return response;
                    });
                };
            }
        }, {
            key: 'prepare',
            value: function prepare(data) {
                var object = {};
                (0, _utils.extend)(object, data);

                for (var key in object) {
                    if (!object.hasOwnProperty(key)) {
                        continue;
                    }
                    var value = object[key];
                    if (value instanceof Function) {
                        object[key] = value();
                    }
                }
                return object;
            }
        }, {
            key: 'createLazyPropertyLoader',
            value: function createLazyPropertyLoader(key) {
                if (this.api[key]) {
                    return;
                }
                var self = this;
                this.api[key] = this.fx.lazyProperties.reduce(function (map, name) {
                    map[name] = function () {
                        //console.log('y', self.api[key]);
                        return self.api[key][name];
                    };
                    return map;
                }, {});
            }
        }]);
        return EndpointFixture;
    }();

    return new Fixtures();
};

module.exports = exports['default'];
//# sourceMappingURL=fixtures2.js.map