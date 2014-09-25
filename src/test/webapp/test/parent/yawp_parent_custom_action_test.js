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

		yawp(parent).put('touched').done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'touched xpto');
			t.start();
		});
	});

	t.asyncTest("over collection", function(assert) {
		expect(3);

		fx.parent('parent1', {
			name : 'xpto1'
		});

		fx.parent('parent2', {
			name : 'xpto2'
		});

		yawp('/parents').put('touched').done(function(parents) {
			assert.equal(parents.length, 2);
			assert.equal(parents[0].name, 'touched xpto1');
			assert.equal(parents[1].name, 'touched xpto2');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);