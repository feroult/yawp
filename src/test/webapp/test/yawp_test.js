(function(t, yawp, fx) {

	t.module('persistence');

	t.testStart(function() {
		fx.reset();
	});

	t.asyncTest("save new parent", function(assert) {
		expect(1);

		var kurt = {
			name : 'kurt'
		};

		yawp.save('/parents', kurt).done(function(retrievedKurt) {
			assert.equal(retrievedKurt.name, 'kurt');
			t.start();
		});
	});

	t.asyncTest("save existing parent", function(assert) {
		expect(1);

		var kurt = fx.parent('kurt', {
			name : 'kurt'
		});

		kurt.name = 'changed kurt';

		yawp.save(kurt.id, kurt).done(function(retrievedKurt) {
			assert.equal(retrievedKurt.name, 'changed kurt');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);