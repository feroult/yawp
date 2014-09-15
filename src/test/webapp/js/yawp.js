(function($) {

	var baseUrl = '/api';

	function defaultAjax(type, options) {
		var request = $.ajax({
			type : type,
			url : options.url,
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
				object : object
			};
		}

		return {
			endpoint : args[0],
			object : args[1]
		};
	}

	function save() {
		var args = parseArgs(arguments);

		var options = {
			url : baseUrl + args.endpoint,
			data : JSON.stringify(args.object),
		};

		return defaultAjax('POST', options);
	}

	function idRef(id) {
		var options = {
			url : baseUrl + id
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

	function where(endpoint, condition) {
		var options = {
			url : baseUrl + endpoint,
			data : {
				q : JSON.stringify(condition)
			}
		};

		return defaultAjax('GET', options);
	}

	function query(endpoint) {
		var condition = {};

		function where(value) {
			condition.where = value;
		}

		return {
			list : list
		}
	}

	var api = {
		save : save,
		idRef : idRef,
		query : query
	};

	window.yawp = api;

})(jQuery);
