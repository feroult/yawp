(function(t, yawp, fx) {

	yawp.config(function(c) {
		c.baseUrl('/api');
	});

	fx.config(function(c) {
		c.baseUrl('/fixtures');
		c.resetUrl('/fixtures/delete_all');

		c.bind('person', '/people');
	});

	function moduledef(module, options) {
		t.module(module);
		if (options.testStart) {
			t.testStart(function(details) {
				if (details.module != module) {
					return;
				}
				options.testStart();
			});
		}
	}

	t.moduledef = moduledef;

})(QUnit, yawp, yawp.fixtures);