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

		// yawp.idRef(parent.id).save('/children',
		// child).done(function(retrievedChild) {
		// assert.equal(retrievedChild.name, 'xpto');
		// assert.equal(retrievedChild.parentId, parent.id);
		// t.start();
		// });

		yawp.save(parent.id + '/children', child).done(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'xpto');
			assert.equal(retrievedChild.parentId, parent.id);
			t.start();
		});
	});

	//
	// t.asyncTest("create array", function(assert) {
	// expect(3);
	//
	// var children = [ {
	// name : 'xpto1'
	// }, {
	// name : 'xpto2'
	// } ];
	//
	// yawp.save('/children', children).done(function(retrievedParents) {
	// assert.equal(retrievedParents.length, 2)
	// assert.equal(retrievedParents[0].name, 'xpto1');
	// assert.equal(retrievedParents[1].name, 'xpto2');
	// t.start();
	// });
	// });
	//
	// t.asyncTest("update", function(assert) {
	// expect(1);
	//
	// var child = fx.child('child', {
	// name : 'xpto'
	// });
	//
	// child.name = 'changed xpto';
	//
	// yawp.save(child).done(function(retrievedParent) {
	// assert.equal(retrievedParent.name, 'changed xpto');
	// t.start();
	// });
	// });
	//
	// t.asyncTest("show", function(assert) {
	// expect(1);
	//
	// var child = fx.child('child', {
	// name : 'xpto'
	// });
	//
	// yawp.idRef(child.id).fetch(function(retrievedParent) {
	// assert.equal(retrievedParent.name, 'xpto');
	// t.start();
	// });
	//
	// });
	//
	// t.asyncTest("index", function(assert) {
	// expect(3);
	//
	// fx.child('child1', {
	// name : 'xpto1'
	// });
	//
	// fx.child('child2', {
	// name : 'xpto2'
	// });
	//
	// var order = [ {
	// p : 'name'
	// } ];
	//
	// function eventually(children) {
	// return children.length == 2 && children[0].name == 'xpto1' &&
	// children[1].name
	// == 'xpto2';
	// }
	//
	// function retry() {
	// yawp.query('/children').order(order).list(function(children) {
	// if (!eventually(children)) {
	// retry();
	// return;
	// }
	//
	// assert.equal(children.length, 2);
	// assert.equal(children[0].name, 'xpto1');
	// assert.equal(children[1].name, 'xpto2');
	// t.start();
	// });
	// }
	//
	// retry();
	// });
	//
	// t.asyncTest("delete", function(assert) {
	// expect(2);
	//
	// var child = fx.child('child', {
	// name : 'xpto'
	// });
	//
	// yawp.idRef(child.id).destroy(function(retrievedParent) {
	// t.equal(child.id, retrievedParent.id);
	//
	// yawp.idRef(child.id).fetch().fail(function(error) {
	// assert.equal(error.status, 404);
	// t.start();
	// });
	// });
	//
	// });

})(QUnit, yawp, yawp.fixtures);