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
		var key;
		for (key in object) {
			return object[key];
		}
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

		function fetch(callback) {
			return defaultAjax('GET', options).done(callback);
		}

		return {
			save : save,
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

	function saveX(object) {
		var options = {
			url : extractId(object),
			data : JSON.stringify(object),
		};

		return defaultAjax('PUT', options);
	}

	function destroyX(object) {
		var options = {
			url : extractId(object)
		};
		return defaultAjax('DELETE', options);
	}

	var api = {
		config : config,
		saveX : saveX,
		destroyX : destroyX
	};

	$.extend(yawp, api);

	window.yawp = yawp;

})(jQuery);
