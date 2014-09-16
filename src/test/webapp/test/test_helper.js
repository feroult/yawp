(function(t) {

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

})(QUnit);