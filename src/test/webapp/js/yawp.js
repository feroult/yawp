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
		return '';
	}

	function query(base) {
		var q = {};

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

		function list(callback) {
			var options = {
				url : base
			}

			if (Object.keys(q).length > 0) {
				options.data = {
					q : JSON.stringify(q)
				};
			}

			return defaultAjax('GET', options).done(callback);
		}

		return {
			where : where,
			order : order,
			sort : sort,
			list : list
		};
	}

	function repository(base) {
		var options = {
			url : base
		};

		function save(object) {
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

		function fetch(callback) {
			return defaultAjax('GET', options).done(callback);
		}

		return {
			save : save,
			update : update,
			destroy : destroy,
			fetch : fetch
		};
	}

	function actions(base) {
		var options = {
			url : base
		};

		function put(action) {
			var putOptions = $.extend({}, options);
			putOptions.url = options.url + '/' + action;
			return defaultAjax('PUT', putOptions);
		}

		return {
			put : put
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
