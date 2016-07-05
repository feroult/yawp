'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

exports.default = function (request) {

    // config

    function config(callback) {
        var c = {
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
            method: 'GET'
        }).then(function () {
            cache = {};
            queue = [];
        });
    }

    function prepareObject(data) {
        var object = {};
        (0, _utils.extend)(object, data);
        return new Promise(function (resolve) {
            var lazyProperties = [];
            loadLazyProperties(lazyProperties, object);

            if (!lazyProperties.length) {
                resolve(object);
            }

            var promise = lazyProperties[0]();
            for (var i = 1, l = lazyProperties.length; i < l; i++) {
                promise = promise.then(lazyProperties[i]);
            }

            promise.then(function () {
                resolve(object);
            });
        });
    }

    function loadLazyProperties(lazyProperties, object) {
        var i;
        for (i in object) {
            if (!object.hasOwnProperty(i)) {
                continue;
            }

            var property = object[i];

            if (property instanceof Function) {
                lazyProperties.push(function () {
                    return property().then(function (value) {
                        object[i] = value;
                    });
                });
                continue;
            }

            if (property instanceof Object) {
                loadLazyProperties(lazyProperties, property);
                continue;
            }
        }
    }

    function save(endpoint, parentId, data) {
        if (!endpoint) {
            console.error('not endpoint?!');
        }

        return prepareObject(data).then(function (object) {
            var url = _baseUrl + (parentId ? object[parentId] : '') + endpoint;
            return request(url, null, {
                method: 'POST',
                json: true,
                body: JSON.stringify(object)
            });
        });
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
        result.then(function (object) {
            cache[endpointKey][key] = object;
        });
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
        var enqueue = arguments.length <= 5 || arguments[5] === undefined ? true : arguments[5];

        var object = loadFixtureFromCache(endpointKey, key);
        if (object) {
            return function () {
                return new Promise(function (resolve) {
                    resolve(object);
                });
            };
        }

        var isLazy = !data;

        if (isLazy) {
            if (hasLazy(endpoint, key)) {
                data = lazy[endpoint][key];
            } else {
                throw 'cannot resolve lazy fixture: ' + endpointKey + ' -> ' + key;
            }
        }

        var promiseFn = function promiseFn() {
            return saveFixtureToCache(endpointKey, endpoint, parentId, data, key);
        };

        if (enqueue) {
            queue.push(promiseFn);
        }
        return promiseFn;
    }

    function map(objects) {
        new Promise(function (resolve) {
            var result = {};
            var lazyKeys = [];

            for (var i in objects) {
                var object = objects[i];

                var key = object.key;
                var value = object.value;

                if (key instanceof Function) {
                    lazyKeys.push(function () {
                        return key().then(function (keyValue) {
                            result[keyValue] = value;
                        });
                    });
                    continue;
                }

                result[key] = value;
            }

            if (!lazyKeys.length) {
                resolve(result);
                return;
            }

            var promise = lazyKeys[0]();
            for (var i = 1, l = lazyKeys.length; i < l; i++) {
                promise = promise.then(lazyKeys[i]);
            }

            promise.then(function () {
                resolve(result);
            });
        });
    }

    function mapFn(objects) {
        return function () {
            return map(objects);
        };
    }

    function computeLazyPropertiesApi(apiKey, fixtureKey) {
        var i,
            lazyPropertiesApi = {};

        function addLazyPropertyApi(propertyKey) {
            return function () {
                var saveToCachePromise = api[apiKey](fixtureKey, null, false)();
                return saveToCachePromise.then(function () {
                    return cache[apiKey][fixtureKey][propertyKey];
                });
            };
        }

        for (i = 0; i < _lazyPropertyKeys.length; i++) {
            var propertyKey = _lazyPropertyKeys[i];
            lazyPropertiesApi[propertyKey] = addLazyPropertyApi(propertyKey);
        }

        return lazyPropertiesApi;
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

        lazyApi.map = mapFn;
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
    api.map = mapFn;
    api.config = config;

    return api;
};

var _utils = require('./utils');

var _baseUrl = '/fixtures';
var _resetUrl = '/_ah/yawp/datastore/delete_all';
var _lazyPropertyKeys = ['id']; // needed till harmony proxies

var api = {};

module.exports = exports['default'];
//# sourceMappingURL=fixtures.js.map