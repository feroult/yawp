module.exports = (function () {

	var baseUrl = '/api';

	function config(callback) {
		var c = {
			baseUrl: function (url) {
				baseUrl = url;
			}
		};

		callback(c);
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

	function toUrlParam(jsonParams) {
		return Object.keys(jsonParams).map(function(k) {
			return encodeURIComponent(k) + '=' + encodeURIComponent(jsonParams[k])
		}).join('&')
	}

	function defaultAjax(type, options) {
		var fail
			,done
			,exception
			,then
			,error
			,request
			,url;

		url = baseUrl + options.url + (options.query ? '?' + toUrlParam(options.query) : '');

		var callbacks = {
			fail: function (callback) {
				fail = callback;
				return callbacks;
			},
			done: function (callback) {
				done = callback;
				return callbacks;
			},
			exception: function (callback) {
				exception = callback;
				return callbacks;
			},
			then: function (callback) {
				then = callback;
				return callbacks;
			},
			error: function (callback) {
				error = callback;
				return callbacks;
			}
		};

		request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			if (request.readyState === 4) {
				if(request.status === 200) {
					if(done) {
						done(JSON.parse(request.responseText));
					}
					if(then) {
						then(JSON.parse(request.responseText));
					}
				} else {
					if(fail) {
						fail(request);
					}
					if(error) {
						error(extend({}, request, {responseJSON:JSON.parse(request.responseText)}));
					}
					if(exception) {
						exception(JSON.parse(request.responseText));
					}
				}
			}
		};

		request.open(type, url, options.async);
		request.setRequestHeader("Content-type", "application/json;charset=UTF-8");
		request.send(options.data);

		return callbacks;
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
			var url = baseUrl + options().url + (options().query ? '?' + toUrlParam(options().query) : '');
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
				var object = objects.length == 0 ? null : objects[0];
				if (callback) {
					callback(object);
				}
			});
		}

		function only(callback) {
			return list(function (objects) {
				if (objects.length != 1) {
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
			ajaxOptions.query = extend(ajaxOptions.query, params);
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

		return extend({
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

	return extend(yawp, api);
	
})();
