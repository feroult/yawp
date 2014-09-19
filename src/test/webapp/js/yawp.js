(function($) {

	var baseUrl = '/api';

	function config(callback) {
		var c = {
			baseUrl : function(url) {
				baseUrl = url;
			}
		};

		callback(c);
	}

	function defaultAjax(type, options) {
		var request = $.ajax({
			type : type,
			url : baseUrl + options.url,
			data : options.data,
			async : options.async,
			contentType : 'application/json;charset=UTF-8',
			dataType : 'json'
		});

		return request;
	}

	function extractId(object) {
		if (object.id) {
			return object.id;
		}
		throw 'use yawp(id) if your endpoint does not have a @Id field called id';
	}

	function query(base) {
		var q = {};

		var t;

		var options = {
			url : base
		};

		function where(data) {
			q.where = data;
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

		function transform(data) {
			t = data;
			return this;
		}

		function fetch(callback) {
			if (t) {
				options.data = {
					t : t
				}
			}
			return defaultAjax('GET', options).done(callback);
		}

		function list(callback) {
			if (Object.keys(q).length > 0) {
				options.data = {
					q : JSON.stringify(q)
				};
			}

			return defaultAjax('GET', options).done(callback);
		}

		function first(callback) {
			limit(1);

			return list(function(objects) {
				var object = objects.length == 0 ? null : objects[0];
				if (callback) {
					callback(object);
				}
			});
		}

		function only(callback) {
			return list(function(objects) {
				if (objects.length != 1) {
					throw 'called only but got ' + objects.length + ' results';
				}
				if (callback) {
					callback(objects[0]);
				}
			});
		}

		return {
			transform : transform,
			where : where,
			order : order,
			sort : sort,
			limit : limit,
			fetch : fetch,
			list : list,
			first : first,
			only : only
		};
	}

	function repository(base) {
		var options = {
			url : base
		};

		function create(object) {
			options.data = JSON.stringify(object);
			return defaultAjax('POST', options);
		}

		function update(object) {
			options.data = JSON.stringify(object);
			return defaultAjax('PUT', options);
		}

		function destroy(object) {
			options.data = JSON.stringify(object);
			return defaultAjax('DELETE', options);
		}

		return {
			create : create,
			update : update,
			destroy : destroy
		};
	}

	function actions(base) {
		function options(action) {
			return {
				url : base + '/' + action
			};
		}

		function get(action) {
			return defaultAjax('GET', options(action));
		}

		function put(action) {
			return defaultAjax('PUT', options(action));
		}

		function post(action) {
			return defaultAjax('POST', options(action));
		}

		function _delete(action) {
			return defaultAjax('DELETE', options(action));
		}

		return {
			get : get,
			put : put,
			post : post,
			_delete : _delete
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

		var base = normalize(baseArg);

		function from(parentBaseArg) {
			var parentBase = normalize(parentBaseArg);
			return yawp(parentBase + base);
		}

		return $.extend({
			from : from
		}, query(base), repository(base), actions(base));
	}

	function update(object) {
		var id = extractId(object);
		return yawp(id).update(object);
	}

	function destroy(object) {
		var id = extractId(object);
		return yawp(id).destroy(object);
	}

	var api = {
		config : config,
		update : update,
		destroy : destroy
	};

	window.yawp = $.extend(yawp, api);

})(jQuery);
