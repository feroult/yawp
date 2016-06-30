(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define("yawp", [], factory);
	else if(typeof exports === 'object')
		exports["yawp"] = factory();
	else
		root["yawp"] = factory();
})(this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;
/******/
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	
	var _yawp = __webpack_require__(1);
	
	var _yawp2 = _interopRequireDefault(_yawp);
	
	var _fixtures = __webpack_require__(5);
	
	var _fixtures2 = _interopRequireDefault(_fixtures);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	_yawp2.default.fixtures = _fixtures2.default;
	
	exports.default = _yawp2.default;
	module.exports = exports['default'];

/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _utils = __webpack_require__(2);
	
	var _baseUrl = '/api';
	var _defaultFetchOptions = {};
	
	function config(callback) {
	    var c = {
	        baseUrl: function baseUrl(url) {
	            _baseUrl = url;
	        },
	        defaultFetchOptions: function defaultFetchOptions(options) {
	            _defaultFetchOptions = options;
	        }
	    };
	
	    callback(c);
	}
	
	function defaultAjax(type, options) {
	    var url = _baseUrl + options.url;
	    var query = options.query;
	    var body = options.data;
	    delete options.url;
	    delete options.query;
	
	    options.method = type;
	    options.body = body;
	    (0, _utils.extend)(options, _defaultFetchOptions);
	
	    return (0, _utils.baseAjax)(url, query, options);
	}
	
	function extractId(object) {
	    if (object.id) {
	        return object.id;
	    }
	    throw 'use yawp(id) if your endpoint does not have a @Id field called id';
	}
	
	function query(options) {
	    var q = {};
	
	    function where(data) {
	        if (arguments.length === 1) {
	            q.where = data;
	        } else {
	            q.where = [].slice.call(arguments);
	        }
	        return this;
	    }
	
	    function order(data) {
	        q.order = data;
	        return this;
	    }
	
	    function sort(data) {
	        q.sort = data;
	        return this;
	    }
	
	    function limit(data) {
	        q.limit = data;
	        return this;
	    }
	
	    function fetch(callback) {
	        return defaultAjax('GET', options()).done(callback);
	    }
	
	    function setupQuery() {
	        if (Object.keys(q).length > 0) {
	            options.addQueryParameter('q', JSON.stringify(q));
	        }
	    }
	
	    function url(decode) {
	        setupQuery();
	        var url = _baseUrl + options().url + (options().query ? '?' + toUrlParam(options().query) : '');
	        if (decode) {
	            return decodeURIComponent(url);
	        }
	        return url;
	    }
	
	    function list(callback) {
	        setupQuery();
	        return defaultAjax('GET', options()).done(callback);
	    }
	
	    function first(callback) {
	        limit(1);
	
	        return list(function (objects) {
	            var object = objects.length === 0 ? null : objects[0];
	            if (callback) {
	                callback(object);
	            }
	        });
	    }
	
	    function only(callback) {
	        return list(function (objects) {
	            if (objects.length !== 1) {
	                throw 'called only but got ' + objects.length + ' results';
	            }
	            if (callback) {
	                callback(objects[0]);
	            }
	        });
	    }
	
	    return {
	        where: where,
	        order: order,
	        sort: sort,
	        limit: limit,
	        fetch: fetch,
	        list: list,
	        first: first,
	        only: only,
	        url: url
	    };
	}
	
	function repository(options) {
	    function create(object) {
	        options().data = JSON.stringify(object);
	        return defaultAjax('POST', options());
	    }
	
	    function update(object) {
	        options().data = JSON.stringify(object);
	        return defaultAjax('PUT', options());
	    }
	
	    function patch(object) {
	        options().data = JSON.stringify(object);
	        return defaultAjax('PATCH', options());
	    }
	
	    function destroy() {
	        return defaultAjax('DELETE', options());
	    }
	
	    return {
	        create: create,
	        update: update,
	        patch: patch,
	        destroy: destroy
	    };
	}
	
	function actions(options) {
	    function actionOptions(action) {
	        options().url += '/' + action;
	        return options();
	    }
	
	    function json(object) {
	        options.setJson(object);
	        return this;
	    }
	
	    function params(params) {
	        options.addQueryParameters(params);
	        return this;
	    }
	
	    function get(action) {
	        return defaultAjax('GET', actionOptions(action));
	    }
	
	    function put(action) {
	        return defaultAjax('PUT', actionOptions(action));
	    }
	
	    function _patch(action) {
	        return defaultAjax('PATCH', actionOptions(action));
	    }
	
	    function post(action) {
	        return defaultAjax('POST', actionOptions(action));
	    }
	
	    function _delete(action) {
	        return defaultAjax('DELETE', actionOptions(action));
	    }
	
	    return {
	        json: json,
	        params: params,
	        get: get,
	        put: put,
	        _patch: _patch,
	        post: post,
	        _delete: _delete
	    };
	}
	
	function yawp(baseArg) {
	    function normalize(arg) {
	        if (!arg) {
	            return '';
	        }
	        if (arg instanceof Object) {
	            return extractId(arg);
	        }
	        return arg;
	    }
	
	    var ajaxOptions = {
	        url: normalize(baseArg),
	        async: true
	    };
	
	    function options() {
	        return ajaxOptions;
	    }
	
	    options.setJson = function (object) {
	        ajaxOptions.data = JSON.stringify(object);
	    };
	
	    options.addQueryParameters = function (params) {
	        ajaxOptions.query = (0, _utils.extend)(ajaxOptions.query, params);
	    };
	
	    options.addQueryParameter = function (key, value) {
	        if (!ajaxOptions.query) {
	            ajaxOptions.query = {};
	        }
	        ajaxOptions.query[key] = value;
	    };
	
	    function from(parentBaseArg) {
	        var parentBase = normalize(parentBaseArg);
	        options().url = parentBase + options().url;
	        return this;
	    }
	
	    function transform(t) {
	        options.addQueryParameter('t', t);
	        return this;
	    }
	
	    function sync() {
	        ajaxOptions.async = false;
	        return this;
	    }
	
	    return (0, _utils.extend)({
	        from: from,
	        transform: transform,
	        sync: sync
	    }, query(options), repository(options), actions(options));
	}
	
	function update(object) {
	    var id = extractId(object);
	    return yawp(id).update(object);
	}
	
	function patch(object) {
	    var id = extractId(object);
	    return yawp(id).patch(object);
	}
	
	function destroy(object) {
	    var id = extractId(object);
	    return yawp(id).destroy(object);
	}
	
	var api = {
	    config: config,
	    update: update,
	    patch: patch,
	    destroy: destroy
	};
	
	exports.default = (0, _utils.extend)(yawp, api);
	module.exports = exports['default'];

/***/ },
/* 2 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	exports.extend = extend;
	exports.baseAjax = baseAjax;
	//require('babel-polyfill');
	
	if (typeof self !== 'undefined') {
	    //require('es6-promise').polyfill();
	    __webpack_require__(3);
	}
	
	function extend() {
	    var result = arguments[0] || {};
	
	    for (var i = 1, l = arguments.length; i < l; i++) {
	        var obj = arguments[i];
	        for (var attrname in obj) {
	            result[attrname] = obj[attrname];
	        }
	    }
	
	    return result;
	}
	
	function baseAjax(url, query, options) {
	    var _fail, _done, _exception, _then, _error;
	
	    var callbacks = {
	        fail: function fail(callback) {
	            _fail = callback;
	            return callbacks;
	        },
	        done: function done(callback) {
	            _done = callback;
	            return callbacks;
	        },
	        exception: function exception(callback) {
	            _exception = callback;
	            return callbacks;
	        },
	        then: function then(callback) {
	            _then = callback;
	            return callbacks;
	        },
	        error: function error(callback) {
	            _error = callback;
	            return callbacks;
	        }
	    };
	
	    url += query ? '?' + toUrlParam(options.query) : '';
	
	    options.headers = options.headers || {};
	    extend(options.headers, {
	        'Accept': 'application/json',
	        'Content-Type': 'application/json'
	    });
	
	    console.log('url', url);
	    console.log('options', options);
	
	    //(async function () {
	    //    let response = await fetch(url, options);
	    //    let json = await response.json();
	    //    done && done(json);
	    //})();
	
	    fetch(url, options).then(function (response) {
	        return response.json();
	    }).then(function (response) {
	        _done && _done(response);
	        _then && _then(response);
	    }).catch(function (err) {
	        _fail && _fail(err);
	        _error && _error(err);
	        _exception && _exception(err);
	    });
	
	    return callbacks;
	}
	
	function toUrlParam(jsonParams) {
	    return Object.keys(jsonParams).map(function (k) {
	        return encodeURIComponent(k) + '=' + encodeURIComponent(jsonParams[k]);
	    }).join('&');
	}

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	// the whatwg-fetch polyfill installs the fetch() function
	// on the global object (window or self)
	//
	// Return that as the export for use in Webpack, Browserify etc.
	__webpack_require__(4);
	module.exports = self.fetch.bind(self);


/***/ },
/* 4 */
/***/ function(module, exports) {

	(function(self) {
	  'use strict';
	
	  if (self.fetch) {
	    return
	  }
	
	  var support = {
	    searchParams: 'URLSearchParams' in self,
	    iterable: 'Symbol' in self && 'iterator' in Symbol,
	    blob: 'FileReader' in self && 'Blob' in self && (function() {
	      try {
	        new Blob()
	        return true
	      } catch(e) {
	        return false
	      }
	    })(),
	    formData: 'FormData' in self,
	    arrayBuffer: 'ArrayBuffer' in self
	  }
	
	  function normalizeName(name) {
	    if (typeof name !== 'string') {
	      name = String(name)
	    }
	    if (/[^a-z0-9\-#$%&'*+.\^_`|~]/i.test(name)) {
	      throw new TypeError('Invalid character in header field name')
	    }
	    return name.toLowerCase()
	  }
	
	  function normalizeValue(value) {
	    if (typeof value !== 'string') {
	      value = String(value)
	    }
	    return value
	  }
	
	  // Build a destructive iterator for the value list
	  function iteratorFor(items) {
	    var iterator = {
	      next: function() {
	        var value = items.shift()
	        return {done: value === undefined, value: value}
	      }
	    }
	
	    if (support.iterable) {
	      iterator[Symbol.iterator] = function() {
	        return iterator
	      }
	    }
	
	    return iterator
	  }
	
	  function Headers(headers) {
	    this.map = {}
	
	    if (headers instanceof Headers) {
	      headers.forEach(function(value, name) {
	        this.append(name, value)
	      }, this)
	
	    } else if (headers) {
	      Object.getOwnPropertyNames(headers).forEach(function(name) {
	        this.append(name, headers[name])
	      }, this)
	    }
	  }
	
	  Headers.prototype.append = function(name, value) {
	    name = normalizeName(name)
	    value = normalizeValue(value)
	    var list = this.map[name]
	    if (!list) {
	      list = []
	      this.map[name] = list
	    }
	    list.push(value)
	  }
	
	  Headers.prototype['delete'] = function(name) {
	    delete this.map[normalizeName(name)]
	  }
	
	  Headers.prototype.get = function(name) {
	    var values = this.map[normalizeName(name)]
	    return values ? values[0] : null
	  }
	
	  Headers.prototype.getAll = function(name) {
	    return this.map[normalizeName(name)] || []
	  }
	
	  Headers.prototype.has = function(name) {
	    return this.map.hasOwnProperty(normalizeName(name))
	  }
	
	  Headers.prototype.set = function(name, value) {
	    this.map[normalizeName(name)] = [normalizeValue(value)]
	  }
	
	  Headers.prototype.forEach = function(callback, thisArg) {
	    Object.getOwnPropertyNames(this.map).forEach(function(name) {
	      this.map[name].forEach(function(value) {
	        callback.call(thisArg, value, name, this)
	      }, this)
	    }, this)
	  }
	
	  Headers.prototype.keys = function() {
	    var items = []
	    this.forEach(function(value, name) { items.push(name) })
	    return iteratorFor(items)
	  }
	
	  Headers.prototype.values = function() {
	    var items = []
	    this.forEach(function(value) { items.push(value) })
	    return iteratorFor(items)
	  }
	
	  Headers.prototype.entries = function() {
	    var items = []
	    this.forEach(function(value, name) { items.push([name, value]) })
	    return iteratorFor(items)
	  }
	
	  if (support.iterable) {
	    Headers.prototype[Symbol.iterator] = Headers.prototype.entries
	  }
	
	  function consumed(body) {
	    if (body.bodyUsed) {
	      return Promise.reject(new TypeError('Already read'))
	    }
	    body.bodyUsed = true
	  }
	
	  function fileReaderReady(reader) {
	    return new Promise(function(resolve, reject) {
	      reader.onload = function() {
	        resolve(reader.result)
	      }
	      reader.onerror = function() {
	        reject(reader.error)
	      }
	    })
	  }
	
	  function readBlobAsArrayBuffer(blob) {
	    var reader = new FileReader()
	    reader.readAsArrayBuffer(blob)
	    return fileReaderReady(reader)
	  }
	
	  function readBlobAsText(blob) {
	    var reader = new FileReader()
	    reader.readAsText(blob)
	    return fileReaderReady(reader)
	  }
	
	  function Body() {
	    this.bodyUsed = false
	
	    this._initBody = function(body) {
	      this._bodyInit = body
	      if (typeof body === 'string') {
	        this._bodyText = body
	      } else if (support.blob && Blob.prototype.isPrototypeOf(body)) {
	        this._bodyBlob = body
	      } else if (support.formData && FormData.prototype.isPrototypeOf(body)) {
	        this._bodyFormData = body
	      } else if (support.searchParams && URLSearchParams.prototype.isPrototypeOf(body)) {
	        this._bodyText = body.toString()
	      } else if (!body) {
	        this._bodyText = ''
	      } else if (support.arrayBuffer && ArrayBuffer.prototype.isPrototypeOf(body)) {
	        // Only support ArrayBuffers for POST method.
	        // Receiving ArrayBuffers happens via Blobs, instead.
	      } else {
	        throw new Error('unsupported BodyInit type')
	      }
	
	      if (!this.headers.get('content-type')) {
	        if (typeof body === 'string') {
	          this.headers.set('content-type', 'text/plain;charset=UTF-8')
	        } else if (this._bodyBlob && this._bodyBlob.type) {
	          this.headers.set('content-type', this._bodyBlob.type)
	        } else if (support.searchParams && URLSearchParams.prototype.isPrototypeOf(body)) {
	          this.headers.set('content-type', 'application/x-www-form-urlencoded;charset=UTF-8')
	        }
	      }
	    }
	
	    if (support.blob) {
	      this.blob = function() {
	        var rejected = consumed(this)
	        if (rejected) {
	          return rejected
	        }
	
	        if (this._bodyBlob) {
	          return Promise.resolve(this._bodyBlob)
	        } else if (this._bodyFormData) {
	          throw new Error('could not read FormData body as blob')
	        } else {
	          return Promise.resolve(new Blob([this._bodyText]))
	        }
	      }
	
	      this.arrayBuffer = function() {
	        return this.blob().then(readBlobAsArrayBuffer)
	      }
	
	      this.text = function() {
	        var rejected = consumed(this)
	        if (rejected) {
	          return rejected
	        }
	
	        if (this._bodyBlob) {
	          return readBlobAsText(this._bodyBlob)
	        } else if (this._bodyFormData) {
	          throw new Error('could not read FormData body as text')
	        } else {
	          return Promise.resolve(this._bodyText)
	        }
	      }
	    } else {
	      this.text = function() {
	        var rejected = consumed(this)
	        return rejected ? rejected : Promise.resolve(this._bodyText)
	      }
	    }
	
	    if (support.formData) {
	      this.formData = function() {
	        return this.text().then(decode)
	      }
	    }
	
	    this.json = function() {
	      return this.text().then(JSON.parse)
	    }
	
	    return this
	  }
	
	  // HTTP methods whose capitalization should be normalized
	  var methods = ['DELETE', 'GET', 'HEAD', 'OPTIONS', 'POST', 'PUT']
	
	  function normalizeMethod(method) {
	    var upcased = method.toUpperCase()
	    return (methods.indexOf(upcased) > -1) ? upcased : method
	  }
	
	  function Request(input, options) {
	    options = options || {}
	    var body = options.body
	    if (Request.prototype.isPrototypeOf(input)) {
	      if (input.bodyUsed) {
	        throw new TypeError('Already read')
	      }
	      this.url = input.url
	      this.credentials = input.credentials
	      if (!options.headers) {
	        this.headers = new Headers(input.headers)
	      }
	      this.method = input.method
	      this.mode = input.mode
	      if (!body) {
	        body = input._bodyInit
	        input.bodyUsed = true
	      }
	    } else {
	      this.url = input
	    }
	
	    this.credentials = options.credentials || this.credentials || 'omit'
	    if (options.headers || !this.headers) {
	      this.headers = new Headers(options.headers)
	    }
	    this.method = normalizeMethod(options.method || this.method || 'GET')
	    this.mode = options.mode || this.mode || null
	    this.referrer = null
	
	    if ((this.method === 'GET' || this.method === 'HEAD') && body) {
	      throw new TypeError('Body not allowed for GET or HEAD requests')
	    }
	    this._initBody(body)
	  }
	
	  Request.prototype.clone = function() {
	    return new Request(this)
	  }
	
	  function decode(body) {
	    var form = new FormData()
	    body.trim().split('&').forEach(function(bytes) {
	      if (bytes) {
	        var split = bytes.split('=')
	        var name = split.shift().replace(/\+/g, ' ')
	        var value = split.join('=').replace(/\+/g, ' ')
	        form.append(decodeURIComponent(name), decodeURIComponent(value))
	      }
	    })
	    return form
	  }
	
	  function headers(xhr) {
	    var head = new Headers()
	    var pairs = (xhr.getAllResponseHeaders() || '').trim().split('\n')
	    pairs.forEach(function(header) {
	      var split = header.trim().split(':')
	      var key = split.shift().trim()
	      var value = split.join(':').trim()
	      head.append(key, value)
	    })
	    return head
	  }
	
	  Body.call(Request.prototype)
	
	  function Response(bodyInit, options) {
	    if (!options) {
	      options = {}
	    }
	
	    this.type = 'default'
	    this.status = options.status
	    this.ok = this.status >= 200 && this.status < 300
	    this.statusText = options.statusText
	    this.headers = options.headers instanceof Headers ? options.headers : new Headers(options.headers)
	    this.url = options.url || ''
	    this._initBody(bodyInit)
	  }
	
	  Body.call(Response.prototype)
	
	  Response.prototype.clone = function() {
	    return new Response(this._bodyInit, {
	      status: this.status,
	      statusText: this.statusText,
	      headers: new Headers(this.headers),
	      url: this.url
	    })
	  }
	
	  Response.error = function() {
	    var response = new Response(null, {status: 0, statusText: ''})
	    response.type = 'error'
	    return response
	  }
	
	  var redirectStatuses = [301, 302, 303, 307, 308]
	
	  Response.redirect = function(url, status) {
	    if (redirectStatuses.indexOf(status) === -1) {
	      throw new RangeError('Invalid status code')
	    }
	
	    return new Response(null, {status: status, headers: {location: url}})
	  }
	
	  self.Headers = Headers
	  self.Request = Request
	  self.Response = Response
	
	  self.fetch = function(input, init) {
	    return new Promise(function(resolve, reject) {
	      var request
	      if (Request.prototype.isPrototypeOf(input) && !init) {
	        request = input
	      } else {
	        request = new Request(input, init)
	      }
	
	      var xhr = new XMLHttpRequest()
	
	      function responseURL() {
	        if ('responseURL' in xhr) {
	          return xhr.responseURL
	        }
	
	        // Avoid security warnings on getResponseHeader when not allowed by CORS
	        if (/^X-Request-URL:/m.test(xhr.getAllResponseHeaders())) {
	          return xhr.getResponseHeader('X-Request-URL')
	        }
	
	        return
	      }
	
	      xhr.onload = function() {
	        var options = {
	          status: xhr.status,
	          statusText: xhr.statusText,
	          headers: headers(xhr),
	          url: responseURL()
	        }
	        var body = 'response' in xhr ? xhr.response : xhr.responseText
	        resolve(new Response(body, options))
	      }
	
	      xhr.onerror = function() {
	        reject(new TypeError('Network request failed'))
	      }
	
	      xhr.ontimeout = function() {
	        reject(new TypeError('Network request failed'))
	      }
	
	      xhr.open(request.method, request.url, true)
	
	      if (request.credentials === 'include') {
	        xhr.withCredentials = true
	      }
	
	      if ('responseType' in xhr && support.blob) {
	        xhr.responseType = 'blob'
	      }
	
	      request.headers.forEach(function(value, name) {
	        xhr.setRequestHeader(name, value)
	      })
	
	      xhr.send(typeof request._bodyInit === 'undefined' ? null : request._bodyInit)
	    })
	  }
	  self.fetch.polyfill = true
	})(typeof self !== 'undefined' ? self : this);


/***/ },
/* 5 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _utils = __webpack_require__(2);
	
	var _baseUrl = '/fixtures';
	var _resetUrl = '/_ah/yawp/datastore/delete_all';
	var _lazyPropertyKeys = ['id']; // needed till harmony proxies
	
	var api = {};
	
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
	        bind: function bind(key, endpoint, parentId) {
	            api[key] = _bind(fixture, endpoint, parentId);
	        }
	    };
	
	    callback(c);
	
	    api.lazy = computeLazyApi();
	}
	
	// lib
	
	var lazy = {};
	var lazyProperties = {};
	
	var load = {};
	
	function _bind(fn, endpoint, parentId) {
	    var bindFn = function bindFn() {
	        var args = Array.prototype.slice.call(arguments, 0);
	        args.unshift(parentId);
	        args.unshift(endpoint);
	        return fn.apply(this, args);
	    };
	    bindFn.endpoint = endpoint;
	    return bindFn;
	}
	
	function reset() {
	    (0, _utils.baseAjax)(_resetUrl, null, {
	        method: 'GET',
	        async: false
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
	    (0, _utils.extend)(newData, data);
	    parseFunctions(newData);
	    return JSON.stringify(newData);
	}
	
	function save(endpoint, parentId, data) {
	    var retrievedObject = null;
	
	    if (!endpoint) {
	        console.error('not endpoint?!');
	    }
	
	    var url = _baseUrl + (parentId ? data[parentId] : '') + endpoint;
	    var query = null;
	
	    (0, _utils.baseAjax)(url, query, {
	        method: 'POST',
	        async: false,
	        body: prepareDataJSON(data)
	    }).done(function (retrievedData) {
	        retrievedObject = retrievedData;
	    }).fail(function (data) {
	        throw Error('error: ' + data);
	    });
	
	    console.log('r', retrievedObject);
	
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
	
	api.lazy = computeLazyApi();
	api.reset = reset;
	api.map = map;
	api.config = config;
	
	exports.default = api;
	module.exports = exports['default'];

/***/ }
/******/ ])
});
;
//# sourceMappingURL=yawp.js.map