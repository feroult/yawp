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
		function fetch(callback) {
			var options = {
				url : baseUrl + id
			};
			defaultAjax('GET', options).done(callback);
		}

		return {
			fetch : fetch
		}
	}

	var api = {
		save : save,
		idRef : idRef
	};

	window.yawp = api;

})(jQuery);