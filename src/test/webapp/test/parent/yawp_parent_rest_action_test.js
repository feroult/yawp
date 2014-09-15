(function(t, yawp, fx) {

	t.module('persistence');

	t.testStart(function() {
		fx.reset();
	});

	t.asyncTest("create", function(assert) {
		expect(1);

		var parent = {
			name : 'xpto'
		};

		yawp.save('/parents', parent).done(function(retrievedParents) {
			assert.equal(retrievedParents.name, 'xpto');
			t.start();
		});
	});

	t.asyncTest("create array", function(assert) {
		expect(1);

		var parents = [ {
			name : 'xpto1'
		}, {
			name : 'xpto2'
		} ];

		yawp.save('/parents', parents).done(function(retrievedParents) {
			assert.equal(retrievedKurt.name, 'xpto');
			t.start();
		});
	});

	// t.asyncTest("update", function(assert) {
	// expect(1);
	//
	// var kurt = fx.parent('kurt', {
	// name : 'kurt'
	// });
	//
	// kurt.name = 'changed kurt';
	//
	// yawp.save(kurt.id, kurt).done(function(retrievedKurt) {
	// assert.equal(retrievedKurt.name, 'changed kurt');
	// t.stop();
	// });
	// });

})(QUnit, yawp, yawp.fixtures);