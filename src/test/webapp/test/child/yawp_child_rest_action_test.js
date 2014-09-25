(function(t, yawp, fx) {

	t.moduledef('child rest action', {
		testStart : function() {
			fx.reset();

			fx.parent('parent', {});
		}
	});

	t.asyncTest("create", function(assert) {
		expect(2);

		var parent = fx.parent('parent');

		var child = {
			name : 'xpto',
			parentId : parent.id
		};

		yawp('/children').from(parent).create(child).done(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'xpto');
			assert.equal(retrievedChild.parentId, parent.id);
			t.start();
		});
	});

	t.asyncTest("create array", function(assert) {
		expect(5);

		var parent = fx.parent('parent');

		var children = [ {
			name : 'xpto1',
			parentId : parent.id
		}, {
			name : 'xpto2',
			parentId : parent.id
		} ];

		yawp('/children').from(parent).create(children).done(function(retrievedChidren) {
			assert.equal(retrievedChidren.length, 2)
			assert.equal(retrievedChidren[0].name, 'xpto1');
			assert.equal(retrievedChidren[1].name, 'xpto2');
			assert.equal(retrievedChidren[0].parentId, parent.id);
			assert.equal(retrievedChidren[1].parentId, parent.id);
			t.start();
		});
	});

	t.asyncTest("update", function(assert) {
		expect(2);

		var parent = fx.parent('parent');

		var child = fx.child('child', {
			name : 'xpto',
			parentId : parent.id
		});

		child.name = 'changed xpto';

		yawp(child.id).update(child).done(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'changed xpto');
			assert.equal(retrievedChild.parentId, parent.id);
			t.start();
		});
	});

	t.asyncTest("show", function(assert) {
		expect(1);

		var parent = fx.parent('parent');

		var child = fx.child('child', {
			name : 'xpto',
			parentId : parent.id
		});

		yawp(child).fetch(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'xpto');
			t.start();
		});

	});

	t.asyncTest("index", function(assert) {
		expect(5);

		var parent = fx.parent('parent');

		fx.child('child1', {
			name : 'xpto1',
			parentId : parent.id
		});

		fx.child('child2', {
			name : 'xpto2',
			parentId : parent.id
		});

		var order = [ {
			p : 'name'
		} ];

		yawp('/children').from(parent).order(order).list(function(children) {
			assert.equal(children.length, 2);
			assert.equal(children[0].name, 'xpto1');
			assert.equal(children[1].name, 'xpto2');
			assert.equal(children[0].parentId, parent.id);
			assert.equal(children[1].parentId, parent.id);
			t.start();
		});
	});

	t.asyncTest("index global", function(assert) {
		expect(11);

		var parent1 = fx.parent('parent1', {});
		var parent2 = fx.parent('parent2', {});

		fx.child('child1', {
			name : 'xpto1',
			parentId : parent1.id
		});

		fx.child('child2', {
			name : 'xpto2',
			parentId : parent2.id
		});

		yawp('/children').from(parent1).list(function(children) {
			assert.equal(children.length, 1);
			assert.equal(children[0].name, 'xpto1');
			assert.equal(children[0].parentId, parent1.id);

		}).then(function() {

			yawp('/children').from(parent2).list(function(children) {
				assert.equal(children.length, 1);
				assert.equal(children[0].name, 'xpto2');
				assert.equal(children[0].parentId, parent2.id);
			});

		}).then(function() {

			var order = [ {
				p : 'name'
			} ];

			yawp('/children').order(order).list(function(children) {

				assert.equal(children.length, 2);
				assert.equal(children[0].name, 'xpto1');
				assert.equal(children[1].name, 'xpto2');
				assert.equal(children[0].parentId, parent1.id);
				assert.equal(children[1].parentId, parent2.id);
				t.start();
			});
		});
	});

	t.asyncTest("destroy", function(assert) {
		expect(2);

		var parent = fx.parent("parent");

		var child = fx.child('child', {
			name : 'xpto',
			parentId : parent.id
		});

		yawp(child.id).destroy(child).done(function(retrievedId) {
			t.equal(child.id, retrievedId);

		}).then(function() {
			yawp(child).fetch().fail(function(error) {
				assert.equal(error.status, 404);
				t.start();
			});
		});

	});

})(QUnit, yawp, yawp.fixtures);