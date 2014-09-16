(function(t, yawp, fx) {

	t.moduledef('parent custom action', {
		testStart : function() {
			fx.reset();
		}
	});

	t.asyncTest("over object", function(assert) {
		expect(1);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp.idRef(parent.id).put('touched').done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'touched xpto');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);