(function(t, yawp, fx) {

	t.moduledef('child query', {
		testStart : function() {
			fx.reset();

			fx.parent('parent1', {
				name : 'xpto1'
			});

			fx.child('child1', {
				name : 'xpto1',
				parentId : fx.parent('parent1').id
			});

			fx.child('child2', {
				name : 'xpto2',
				parentId : fx.parent('parent1').id
			});

			fx.child('child3', {
				name : 'xpto3',
				parentId : fx.parent('parent1').id
			});

			fx.parent('parent2', {
				name : 'xpto2'
			});

			fx.child('child4', {
				name : 'xpto1',
				parentId : fx.parent('parent2').id
			});
		}
	});

	t.asyncTest('where', function(assert) {
		expect(2);

		var parent1 = fx.parent('parent1');

		var where = [ 'name', '=', 'xpto1' ];

		yawp('/children').from(parent1).where(where).list(function(children) {
			assert.equal(children.length, 1);
			assert.equal(children[0].name, 'xpto1');
			t.start();
		});
	});

	t.asyncTest('global query', function(assert) {
		expect(9);

		var parent1 = fx.parent('parent1');
		var parent2 = fx.parent('parent2');

		var order = [ {
			p : 'name'
		} ];

		yawp('/children').order(order).list(function(children) {
			assert.equal(children.length, 4);

			assert.equal(children[0].name, 'xpto1');
			assert.equal(children[1].name, 'xpto1');
			assert.equal(children[2].name, 'xpto2');
			assert.equal(children[3].name, 'xpto3');

			assert.ok(children[0].parentId == parent1.id || children[0].parentId == parent2.id);
			assert.ok(children[1].parentId == parent1.id || children[1].parentId == parent2.id);
			assert.equal(children[2].parentId, parent1.id);
			assert.equal(children[3].parentId, parent1.id);
			t.start();
		});
	});

	t.asyncTest('limit', function(assert) {
		expect(3);

		var parent1 = fx.parent('parent1');

		var order = [ {
			p : 'name'
		} ];

		yawp('/children').from(parent1).order(order).limit(2).list(function(children) {
			assert.equal(children.length, 2);
			assert.equal(children[0].name, 'xpto1');
			assert.equal(children[1].name, 'xpto2');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);
