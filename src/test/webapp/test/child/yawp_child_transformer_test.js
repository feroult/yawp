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

	t.asyncTest("show transformer", function(assert) {
		expect(1);

		var child = fx.child('child1');

		yawp(child).transform('simple').fetch(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'transformed xpto1');
			t.start();
		});

	});

	t.asyncTest("index transformer", function(assert) {
		expect(4);

		var parent1 = fx.parent('parent1');

		var order = [ {
			p : 'name'
		} ];

		yawp('/children').from(parent1).order(order).transform('simple').list(function(children) {
			assert.equal(children.length, 3);
			assert.equal(children[0].name, 'transformed xpto1');
			assert.equal(children[1].name, 'transformed xpto2');
			assert.equal(children[2].name, 'transformed xpto3');
			t.start();
		});

	});

})(QUnit, yawp, yawp.fixtures);
