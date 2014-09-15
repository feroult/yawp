(function(t, yawp, fx) {

	t.module('parent rest action');

	t.testStart(function() {
		fx.reset();
	});

	t.asyncTest("create", function(assert) {
		expect(1);

		var parent = {
			name : 'xpto'
		};

		yawp.save('/parents', parent).done(function(retrievedParents) {
			assert.equal(retrievedParents.name, 'xpto');
			t.start();
		});
	});

	t.asyncTest("create array", function(assert) {
		expect(3);

		var parents = [ {
			name : 'xpto1'
		}, {
			name : 'xpto2'
		} ];

		yawp.save('/parents', parents).done(function(retrievedParents) {
			assert.equal(retrievedParents.length, 2)
			assert.equal(retrievedParents[0].name, 'xpto1');
			assert.equal(retrievedParents[1].name, 'xpto2');
			t.start();
		});
	});

	t.asyncTest("update", function(assert) {
		expect(1);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		parent.name = 'changed xpto';

		yawp.save(parent).done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'changed xpto');
			t.start();
		});
	});

	t.asyncTest("show", function(assert) {
		expect(1);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp.idRef(parent.id).fetch(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'xpto');
			t.start();
		});

	});

	// t.asyncTest("index", function(assert) {
	// expect(1);
	//
	// fx.parent('parent1', {
	// name : 'xpto1'
	// });
	//
	// fx.parent('parent2', {
	// name : 'xpto2'
	// });
	//
	// yawp.query('/parents').where().list(function(retrievedParents) {
	// assert.equal(retrievedParents.length, 2)
	// assert.equal(retrievedParents[0].name, 'xpto1');
	// assert.equal(retrievedParents[1].name, 'xpto2');
	// });
	//
	// });

	t.asyncTest("delete", function(assert) {
		expect(2);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp.idRef(parent.id).destroy(function(retrievedParent) {
			t.equal(parent.id, retrievedParent.id);

			yawp.idRef(parent.id).fetch().fail(function(error) {
				assert.equal(error.status, 404);
				t.start();
			});
		});

	});

})(QUnit, yawp, yawp.fixtures);