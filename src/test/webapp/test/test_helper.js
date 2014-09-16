(function(t) {

	yawp.config(function(c) {
		c.baseUrl('/api');
	});

	yawp.fixtures.config(function(c) {
		c.baseUrl('/fixtures');

		c.bind('parent', '/parents');
		c.bind('child', '/children', 'parentId');
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