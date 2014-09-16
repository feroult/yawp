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

	function save() {
		function parseArgs(args) {
			if (args.length == 1) {
				var object = args[0];
				return {
					endpoint : extractId(object),
					object : object,
					verb : 'PUT'
				};
			}

			return {
				endpoint : args[0],
				object : args[1],
				verb : 'POST'
			};
		}

		var args = parseArgs(arguments);

		var options = {
			url : args.endpoint,
			data : JSON.stringify(args.object),
		};

		return defaultAjax(args.verb, options);
	}

	function destroy(object) {
		var options = {
			url : extractId(object)
		};
		return defaultAjax('DELETE', options);
	}

	function idRef(id) {
		var options = {
			url : id
		};

		function fetch(callback) {
			return defaultAjax('GET', options).done(callback);
		}

		function destroy(callback) {
			return defaultAjax('DELETE', options).done(callback);
		}

		return {
			fetch : fetch,
			destroy : destroy
		}
	}

	function query(endpoint) {
		var q = {};

		var parentId;

		function from(id) {
			parentId = id;
			return this;
		}

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
				url : (parentId ? parentId : '') + endpoint
			}

			if (Object.keys(q).length > 0) {
				options.data = {
					q : JSON.stringify(q)
				};
			}

			return defaultAjax('GET', options).done(callback);
		}

		return {
			from : from,
			where : where,
			order : order,
			sort : sort,
			list : list
		};
	}

	var api = {
		config : config,
		save : save,
		destroy : destroy,
		idRef : idRef,
		query : query
	};

	window.yawp = api;

})(jQuery);
