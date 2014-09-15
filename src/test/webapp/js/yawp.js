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

	function save(endpoint, object) {
		var options = {
			url : baseUrl + endpoint,
			data : JSON.stringify(object)
		};

		return defaultAjax('POST', options);
	}

	function where(endpoint, condition) {
		var options = {
			url : baseUrl + endpoint,
			data : { q: JSON.stringify(condition) }
		};

		return defaultAjax('GET', options);
	}

	var api = {
		save : save,
		where : where
	};

	window.yawp = api;

})(jQuery);