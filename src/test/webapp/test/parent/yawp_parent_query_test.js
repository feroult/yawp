(function(t, yawp, fx) {

	t.moduledef('parent query', {
		testStart : function(details) {
			fx.reset();

			fx.parent('xpto1', {
				name : 'xpto1'
			});

			fx.parent('xpto2', {
				name : 'xpto2'
			});
		}
	});

	t.asyncTest('where', function(assert) {
		expect(2);

		var where = [ 'name', '=', 'xpto1' ];

		function eventually(parents) {
			return parents.length == 1 && parents[0].name == 'xpto1';
		}

		function retry() {
			yawp.query('/parents').where(where).list(function(parents) {
				if (!eventually(parents)) {
					retry();
					return;
				}

				assert.equal(parents.length, 1);
				assert.equal(parents[0].name, 'xpto1');
				t.start();
			});
		}

		retry();
	});

	t.asyncTest('order', function(assert) {
		expect(3);

		var order = [ {
			p : 'name',
			d : 'desc'
		} ];

		function eventually(parents) {
			return parents.length == 2 && parents[0].name == 'xpto2' && parents[1].name == 'xpto1';
		}

		function retry() {
			yawp.query('/parents').order(order).list(function(parents) {
				if (!eventually(parents)) {
					retry();
					return;
				}

				assert.equal(parents.length, 2);
				assert.equal(parents[0].name, 'xpto2');
				assert.equal(parents[1].name, 'xpto1');
				t.start();
			});
		}

		retry();
	});

	t.asyncTest('sort', function(assert) {
		expect(3);

		var sort = [ {
			p : 'name',
			d : 'desc'
		} ];

		function eventually(parents) {
			return parents.length == 2 && parents[0].name == 'xpto2' && parents[1].name == 'xpto1';
		}

		function retry() {
			yawp.query('/parents').sort(sort).list(function(parents) {
				if (!eventually(parents)) {
					retry();
					return;
				}

				assert.equal(parents.length, 2);
				assert.equal(parents[0].name, 'xpto2');
				assert.equal(parents[1].name, 'xpto1');
				t.start();
			});
		}

		retry();
	});

})(QUnit, yawp, yawp.fixtures);
