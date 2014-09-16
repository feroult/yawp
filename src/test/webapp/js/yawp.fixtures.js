(function($) {

	// config

	var baseUrl = '/fixtures';
	var resetUrl = '/_yawp/delete_all';
	var lazyPropertyKeys = [ 'id' ]; // needed till harmony proxies

	var api = {}

	function config(callback) {
		var c = {
			baseUrl : function(url) {
				baseUrl = url;
			},
			resetUrl : function(url) {
				resetUrl = url;
			},
			lazyPropertyKeys : function(array) {
				lazyPropertyKeys = array;
			},
			bind : function(key, endpoint, parentId) {
				api[key] = bind(fixture, endpoint, parentId)
			}
		};

		callback(c);
	}

	// lib

	var lazy = {};
	var lazyProperties = {};

	var load = {};

	function bind(fn, endpoint, parentId) {
		var bindFn = function() {
			args = Array.prototype.slice.call(arguments, 0);
			args.unshift(parentId);
			args.unshift(endpoint);
			return fn.apply(this, args);
		}
		bindFn.endpoint = endpoint;
		return bindFn;
	}

	function reset() {
		$.ajax({
			type : 'GET',
			url : resetUrl,
			async : false,
			contentType : 'application/json;charset=UTF-8',
			dataType : 'json'
		});

		load = {};
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
		$.extend(true, newData, data);
		parseFunctions(newData);
		return JSON.stringify(newData);
	}

	function save(endpoint, parentId, data) {
		var retrievedObject;

		if (!endpoint) {
			console.error('not endpoint?!');
		}

		var request = $.ajax({
			type : 'POST',
			url : baseUrl + (parentId ? data[parentId] : '') + endpoint,
			data : prepareDataJSON(data),
			async : false,
			contentType : 'application/json;charset=UTF-8',
			dataType : 'json',
			success : function(retrievedData) {
				retrievedObject = retrievedData;
			},
			error : function(data) {
				throw Error('error: ' + data);
			}
		});

		return retrievedObject;
	}

	function loadFixture(endpoint, key) {
		if (!load[endpoint]) {
			load[endpoint] = {};
			return null;
		}
		return load[endpoint][key];
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

	function fixture(endpoint, parentId, key, data) {
		var object = loadFixture(endpoint, key);
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

		object = save(endpoint, parentId, data);
		load[endpoint][key] = object;
		return object;
	}

	function map(objects) {
		var result = {};

		for ( var i in objects) {
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
			return function() {
				return api[apiKey](fixtureKey)[propertyKey];
			}
		}

		for (i = 0; i < lazyPropertyKeys.length; i++) {
			var propertyKey = lazyPropertyKeys[i];
			lazyPropertiesApi[propertyKey] = addLazyPropertyApi(propertyKey);
		}

		return lazyPropertiesApi;
	}

	function lazyMap(objects) {
		return function() {
			return map(objects);
		}
	}

	function computeLazyApi() {
		var lazyApi = {};

		function addLazyApi(apiKey, endpoint) {
			return function(fixtureKey, data) {
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
			}
		}

		for ( var apiKey in api) {
			var endpoint = api[apiKey].endpoint;
			lazyApi[apiKey] = addLazyApi(apiKey, endpoint);
		}

		lazyApi.map = lazyMap;

		return lazyApi;
	}

	api.lazy = computeLazyApi();
	api.reset = reset;
	api.map = map;
	api.config = config;

	window.yawp.fixtures = api;

})(jQuery);
