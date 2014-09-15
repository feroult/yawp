(function(t, yawp, fx) {

	t.module('parent query');

	t.testStart(function() {
		fx.reset();
	});

	t.asyncTest('where', function(assert) {
		expect(2);

		fx.parent('xpto1', { name : 'xpto1' });
		fx.parent('xpto2', { name : 'xpto2' });

		yawp.query('/parents').where(['name', '=', 'xpto1' ]).list().done(function(parents) {
			assert.equal(parents.length, 1);
			assert.equal(parents[0].name, 'xpto1');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);
