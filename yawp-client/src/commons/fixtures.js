import { extend } from './utils';

var baseUrl = '/fixtures';
var resetUrl = '/_ah/yawp/datastore/delete_all';
var lazyPropertyKeys = ['id']; // needed till harmony proxies
var async = true;

var api = {};

export default function (request) {

    // config

    function config(callback) {
        var c = {
            async: function (value) {
                async = value;
            },
            baseUrl: function (url) {
                baseUrl = url;
            },
            resetUrl: function (url) {
                resetUrl = url;
            },
            lazyPropertyKeys: function (array) {
                lazyPropertyKeys = array;
            },
            bind: function (endpointKey, endpoint, parentId) {
                api[endpointKey] = bind(fixture, endpointKey, endpoint, parentId);
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

    function bind(fn, endpointKey, endpoint, parentId) {
        var bindFn = function () {
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
        return request(resetUrl, null, {
            method: 'GET',
            async: async
        }).then(() => {
            cache = {};
            queue = [];
        });
    }

    function prepareDataJSON(data) {
        var newData = {};
        extend(newData, data);
        parseFunctions(newData);
        return JSON.stringify(newData);
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

    function prepareObject(data) {
        var object = {};
        extend(object, data);
        return new Promise((resolve) => {
            var lazyProperties = [];
            loadLazyProperties(lazyProperties, object);

            if (!lazyProperties.length) {
                resolve(object);
            }

            var promise = lazyProperties[0]();
            for (var i = 1, l = lazyProperties.length; i < l; i++) {
                promise = promise.then(lazyProperties[i]);
            }

            promise.then(() => {
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
                lazyProperties.push(() => {
                    return property().then((value) => {
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

        return prepareObject(data).then((object) => {
            var url = baseUrl + (parentId ? object[parentId] : '') + endpoint;
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
        result.then((object) => {
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
        let object = loadFixtureFromCache(endpointKey, key);
        if (object) {
            return () => {
                return new Promise((resolve) => {
                    resolve(object);
                });
            }
        }

        if (!data) {
            if (hasLazy(endpoint, key)) {
                data = lazy[endpoint][key];
            } else {
                throw 'cannot resolve lazy fixture: ' + endpointKey + ' -> ' + key;
            }
        }

        var promiseFn = () => {
            return saveFixtureToCache(endpointKey, endpoint, parentId, data, key);
        };
        queue.push(promiseFn);
        return promiseFn;
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
        var i, lazyPropertiesApi = {};

        function addLazyPropertyApi(propertyKey) {
            return function () {
                var saveToCachePromise = api[apiKey](fixtureKey)();
                return saveToCachePromise.then(() => {
                    return cache[apiKey][fixtureKey][propertyKey]
                });
            };
        }

        for (i = 0; i < lazyPropertyKeys.length; i++) {
            var propertyKey = lazyPropertyKeys[i];
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

        promise.then(() => {
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
}