(function(t, yawp, fx) {

	t.module('parent query');

	t.testStart(function() {
		fx.reset();
	});

	t.asyncTest('where', function(assert) {
		expect(2);

		var where = ['name', '=', 'xpto1' ];

		fx.parent('xpto1', { name : 'xpto1' });
		fx.parent('xpto2', { name : 'xpto2' });

		yawp.query('/parents').where(where).list(function(parents) {
			assert.equal(parents.length, 1);
			assert.equal(parents[0].name, 'xpto1');
			t.start();
		});
	});

	t.asyncTest('order', function(assert) {
		expect(3);

		var order = [{ p : 'name', d : 'desc' }];

		fx.parent('xpto1', { name : 'xpto1' });
		fx.parent('xpto2', { name : 'xpto2' });

		yawp.query('/parents').order(order).list(function(parents) {
			assert.equal(parents.length, 2);
			assert.equal(parents[0].name, 'xpto2');
			assert.equal(parents[1].name, 'xpto1');
			t.start();
		});
	});

	t.asyncTest('sort', function(assert) {
		expect(3);

		var sort = [{ p : 'name', d : 'desc' }];

		fx.parent('xpto1', { name : 'xpto1' });
		fx.parent('xpto2', { name : 'xpto2' });

		yawp.query('/parents').sort(sort).list(function(parents) {
			assert.equal(parents.length, 2);
			assert.equal(parents[0].name, 'xpto2');
			assert.equal(parents[1].name, 'xpto1');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);
