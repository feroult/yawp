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

		yawp.save(parent.id + '/children', child).done(function(retrievedChild) {
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

		yawp.save(parent.id + '/children', children).done(function(retrievedChidren) {
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

		yawp.save(child).done(function(retrievedChild) {
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

		yawp.idRef(child.id).fetch(function(retrievedChild) {
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

		yawp.query('/children').from(parent.id).order(order).list(function(children) {
			assert.equal(children.length, 2);
			assert.equal(children[0].name, 'xpto1');
			assert.equal(children[1].name, 'xpto2');
			assert.equal(children[0].parentId, parent.id);
			assert.equal(children[1].parentId, parent.id);
			t.start();
		});
	});
	//
	// t.asyncTest("delete", function(assert) {
	// expect(2);
	//
	// var child = fx.child('child', {
	// name : 'xpto'
	// });
	//
	// yawp.idRef(child.id).destroy(function(retrievedChild) {
	// t.equal(child.id, retrievedChild.id);
	//
	// yawp.idRef(child.id).fetch().fail(function(error) {
	// assert.equal(error.status, 404);
	// t.start();
	// });
	// });
	//
	// });

})(QUnit, yawp, yawp.fixtures);