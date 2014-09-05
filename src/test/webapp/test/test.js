(function(QUnit, yawp, fx) {

	QUnit.module('persistence');

	QUnit.testStart(function() {
		fx.reset();
	});

	QUnit.asyncTest("save parent", function(assert) {
		expect(1);

		var kurt = {
			name : 'kurt'
		};

		yawp.save('/parents', kurt).done(function(retrievedKurt) {
			assert.equal(retrievedKurt.name, kurt.name);
		});
	});

})(QUnit, yawp, yawp.fixtures);