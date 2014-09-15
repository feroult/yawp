(function(t, yawp, fx) {

	t.module('persistence');

	t.testStart(function() {
		fx.reset();
	});

	t.asyncTest("create", function(assert) {
		expect(1);

		yawp.save('/parents', {
			name : 'xpto'
		}).done(function(retrievedKurt) {
			assert.equal(retrievedKurt.name, 'kurt');
			t.start();
		});
	});

//	t.asyncTest("update", function(assert) {
//		expect(1);
//
//		var kurt = fx.parent('kurt', {
//			name : 'kurt'
//		});
//
//		kurt.name = 'changed kurt';
//
//		yawp.save(kurt.id, kurt).done(function(retrievedKurt) {
//			assert.equal(retrievedKurt.name, 'changed kurt');
//			t.stop();
//		});
//	});

})(QUnit, yawp, yawp.fixtures);