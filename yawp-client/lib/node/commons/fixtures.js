'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

exports.default = function (request) {

    // config

    function config(callback) {
        var c = {
            async: function async(value) {
                _async = value;
            },
            baseUrl: function baseUrl(url) {
                _baseUrl = url;
            },
            resetUrl: function resetUrl(url) {
                _resetUrl = url;
            },
            lazyPropertyKeys: function lazyPropertyKeys(array) {
                _lazyPropertyKeys = array;
            },
            bind: function bind(endpointKey, endpoint, parentId) {
                api[endpointKey] = _bind(fixture, endpointKey, endpoint, parentId);
            }
        };

        callback(c);

        api.lazy = computeLazyApi();
    }

    // lib

    var lazy = {};
    var lazyProperties = {};

    var cache = {};
    var queue = [];

    function _bind(fn, endpointKey, endpoint, parentId) {
        var bindFn = function bindFn() {
            var args = Array.prototype.slice.call(arguments, 0);
            args.unshift(parentId);
            args.unshift(endpoint);
            args.unshift(endpointKey);
            return fn.apply(this, args);
        };
        bindFn.endpoint = endpoint;
        return bindFn;
    }

    function reset() {
        return request(_resetUrl, null, {
            method: 'GET',
            async: _async
        }).then(function () {
            cache = {};
            queue = [];
        });
    }

    function parseFunctions(object) {
        var i;
        for (i in object) {
            if (!object.hasOwnProperty(i)) {
                continue;
            }

            var property = object[i];

            if (property instanceof Function) {
                object[i] = property();
                continue;
            }

            if (property instanceof Object) {
                parseFunctions(property);
                continue;
            }
        }
    }

    function prepareDataJSON(data) {
        var newData = {};
        (0, _utils.extend)(newData, data);
        parseFunctions(newData);
        return JSON.stringify(newData);
    }

    function save(endpoint, parentId, data) {
        if (!endpoint) {
            console.error('not endpoint?!');
        }

        var url = _baseUrl + (parentId ? data[parentId] : '') + endpoint;
        var query = null;

        var result = request(url, query, {
            method: 'POST',
            async: _async,
            json: true,
            body: prepareDataJSON(data)
        });

        if (_async) {
            return result;
        }

        var retrievedObject = null;
        result.done(function (retrievedData) {
            retrievedObject = retrievedData;
        }).fail(function (data) {
            throw Error('error: ' + data);
        });
        return retrievedObject;
    }

    function hasLazy(endpoint, key) {
        if (!lazy[endpoint]) {
            return false;
        }
        if (!lazy[endpoint][key]) {
            return false;
        }
        return true;
    }

    function saveFixtureToCache(endpointKey, endpoint, parentId, data, key) {
        var result = save(endpoint, parentId, data);

        if (_async) {
            result.then(function (object) {
                cache[endpointKey][key] = object;
            });
            return result;
        }

        cache[endpointKey][key] = result;
        return result;
    }

    function loadFixtureFromCache(endpointKey, key) {
        if (!cache[endpointKey]) {
            cache[endpointKey] = {};
            return null;
        }
        return cache[endpointKey][key];
    }

    function fixture(endpointKey, endpoint, parentId, key, data) {
        var object = loadFixtureFromCache(endpointKey, key);
        if (object) {
            return object;
        }

        if (!data) {
            if (hasLazy(endpoint, key)) {
                data = lazy[endpoint][key];
            } else {
                return null;
            }
        }

        // TODO: mark for save and cache is called
        if (!_async) {
            return saveFixtureToCache(endpointKey, endpoint, parentId, data, key);
        } else {
            queue.push(function () {
                return saveFixtureToCache(endpointKey, endpoint, parentId, data, key);
            });
        }
    }

    function map(objects) {
        var result = {};

        for (var i in objects) {
            var object = objects[i];

            var key = object.key;
            var value = object.value;

            if (key instanceof Function) {
                key = key();
            }

            result[key] = value;
        }
        return result;
    }

    function computeLazyPropertiesApi(apiKey, fixtureKey) {
        var i,
            lazyPropertiesApi = {};

        function addLazyPropertyApi(propertyKey) {
            return function () {
                return api[apiKey](fixtureKey)[propertyKey];
            };
        }

        for (i = 0; i < _lazyPropertyKeys.length; i++) {
            var propertyKey = _lazyPropertyKeys[i];
            lazyPropertiesApi[propertyKey] = addLazyPropertyApi(propertyKey);
        }

        return lazyPropertiesApi;
    }

    function lazyMap(objects) {
        return function () {
            return map(objects);
        };
    }

    function computeLazyApi() {
        var lazyApi = {};

        function addLazyApi(apiKey, endpoint) {
            return function (fixtureKey, data) {
                if (!lazy[endpoint]) {
                    lazy[endpoint] = {};
                    lazyProperties[endpoint] = {};
                } else if (lazy[endpoint][fixtureKey]) {
                    // lazy fixture already configured, someone is refering a
                    // lazy property.
                    return lazyProperties[endpoint][fixtureKey];
                }

                lazy[endpoint][fixtureKey] = data;
                lazyProperties[endpoint][fixtureKey] = computeLazyPropertiesApi(apiKey, fixtureKey);
            };
        }

        for (var apiKey in api) {
            var endpoint = api[apiKey].endpoint;
            lazyApi[apiKey] = addLazyApi(apiKey, endpoint);
        }

        lazyApi.map = lazyMap;
        return lazyApi;
    }

    function load(callback) {
        if (!queue.length) {
            callback(cache);
            return;
        }

        var promise = queue[0]();
        for (var i = 1, l = queue.length; i < l; i++) {
            promise = promise.then(queue[i]);
        }

        promise.then(function () {
            queue = [];
            callback(cache);
        });
    }

    api.lazy = computeLazyApi();
    api.load = load;
    api.reset = reset;
    api.map = map;
    api.config = config;

    return api;
};

var _utils = require('./utils');

var _baseUrl = '/fixtures';
var _resetUrl = '/_ah/yawp/datastore/delete_all';
var _lazyPropertyKeys = ['id']; // needed till harmony proxies
var _async = true;

var api = {};

module.exports = exports['default'];
//# sourceMappingURL=fixtures.js.map