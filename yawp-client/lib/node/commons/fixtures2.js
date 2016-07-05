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
var DEFAULT_LAZY_PROPERTY_KEYS = ['id']; // needed till harmony proxies

exports.default = function (request) {
    var Fixtures = function () {
        function Fixtures() {
            (0, _classCallCheck3.default)(this, Fixtures);

            this.baseUrl = DEFAULT_BASE_URL;
            this.resetUrl = DEFAULT_RESET_URL;
            this.lazyPropertyKeys = DEFAULT_LAZY_PROPERTY_KEYS;
        }

        (0, _createClass3.default)(Fixtures, [{
            key: 'config',
            value: function config(callback) {
                callback(this);
            }
        }, {
            key: 'reset',
            value: function reset() {
                return request(this.resetUrl, {
                    method: 'GET'
                }).then(function () {
                    // do not pass the result to the upstream
                });
            }
        }, {
            key: 'bind',
            value: function bind(name, path) {
                this[name] = new EndpointFixture(this, name, path).api;
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
                var _this = this;

                return function (key, data) {
                    return function () {
                        return _this.load(key, data);
                    };
                };
            }
        }, {
            key: 'load',
            value: function load(key, data) {
                var _this2 = this;

                var url = this.fx.baseUrl + this.path;
                return request(url, {
                    method: 'POST',
                    json: true,
                    body: JSON.stringify(data)
                }).then(function (object) {
                    _this2.api[key] = object;
                    return object;
                });
            }
        }]);
        return EndpointFixture;
    }();

    return new Fixtures();
};

module.exports = exports['default'];
//# sourceMappingURL=fixtures2.js.map