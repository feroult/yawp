(function(t, yawp, fx) {

	t.moduledef('child transformer', {
		testStart : function() {
			fx.reset();

			fx.parent('parent', {
				name : 'xpto'
			});

			fx.child('child1', {
				name : 'xpto1',
				parentId : fx.parent('parent').id
			});

			fx.child('child2', {
				name : 'xpto2',
				parentId : fx.parent('parent').id
			});

			fx.child('child3', {
				name : 'xpto3',
				parentId : fx.parent('parent').id
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

		var parent = fx.parent('parent');

		var order = [ {
			p : 'name'
		} ];

		yawp('/children').from(parent).order(order).transform('simple').list(function(children) {
			assert.equal(children.length, 3);
			assert.equal(children[0].name, 'transformed xpto1');
			assert.equal(children[1].name, 'transformed xpto2');
			assert.equal(children[2].name, 'transformed xpto3');
			t.start();
		});

	});

	t.asyncTest("create transformer", function(assert) {
		expect(2);

		var parent = fx.parent('parent');

		var child = {
			name : 'xpto',
			parentId : parent.id
		};

		yawp('/children').from(parent).transform('simple').create(child).done(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'transformed xpto');
			assert.equal(retrievedChild.parentId, parent.id);
			t.start();
		});
	});

	t.asyncTest("create array transformer", function(assert) {
		expect(5);

		var parent = fx.parent('parent');

		var children = [ {
			name : 'xpto1',
			parentId : parent.id
		}, {
			name : 'xpto2',
			parentId : parent.id
		} ];

		yawp('/children').from(parent).transform('simple').create(children).done(function(retrievedChidren) {
			assert.equal(retrievedChidren.length, 2)
			assert.equal(retrievedChidren[0].name, 'transformed xpto1');
			assert.equal(retrievedChidren[1].name, 'transformed xpto2');
			assert.equal(retrievedChidren[0].parentId, parent.id);
			assert.equal(retrievedChidren[1].parentId, parent.id);
			t.start();
		});
	});

	t.asyncTest("update transformer", function(assert) {
		expect(2);

		var parent = fx.parent('parent');

		var child = fx.child('child', {
			name : 'xpto',
			parentId : parent.id
		});

		child.name = 'changed xpto';

		yawp(child.id).transform('simple').update(child).done(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'transformed changed xpto');
			assert.equal(retrievedChild.parentId, parent.id);
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);
