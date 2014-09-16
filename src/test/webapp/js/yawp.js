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

	function parseArgs(args) {
		if (args.length == 1) {
			var object = args[0];
			return {
				endpoint : object.id,
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

	function save() {
		var args = parseArgs(arguments);

		var options = {
			url : args.endpoint,
			data : JSON.stringify(args.object),
		};

		return defaultAjax(args.verb, options);
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
				url : endpoint,
				data : {
					q : JSON.stringify(q)
				}
			};

			return defaultAjax('GET', options).done(callback);
		}

		return {
			where : where,
			order : order,
			sort : sort,
			list : list
		};
	}

	var api = {
		config : config,
		save : save,
		idRef : idRef,
		query : query
	};

	window.yawp = api;

})(jQuery);
