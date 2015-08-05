(function(t, yawp, fx) {

	t.moduledef('parent rest action', {
		testStart : function() {
			fx.reset();
		}
	});

	t.asyncTest("create", function(assert) {
		expect(1);

		var parent = {
			name : 'xpto'
		};

		yawp('/parents').create(parent).done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'xpto');
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

		yawp('/parents').create(parents).done(function(retrievedParents) {
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
		yawp.update(parent).done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'changed xpto');
			t.start();
		});
	});

	t.asyncTest("patch", function(assert) {
		expect(2);

		var job = fx.job('coder', {
			name : 'coder'
		});

		var parent = fx.parent('parent', {
			name : 'xpto',
			jobId : job.id
		});

		var parentPatch = {
			id : parent.id,
			name : 'changed xpto'
		};

		yawp.patch(parentPatch).done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'changed xpto');
			assert.equal(retrievedParent.jobId, job.id);
			t.start();
		});
	});

	t.asyncTest("show", function(assert) {
		expect(1);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp(parent).fetch(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'xpto');
			t.start();
		});

	});

	t.asyncTest("index", function(assert) {
		expect(3);

		fx.parent('parent1', {
			name : 'xpto1'
		});

		fx.parent('parent2', {
			name : 'xpto2'
		});

		var order = [ {
			p : 'name'
		} ];

		yawp('/parents').order(order).list(function(parents) {

			assert.equal(parents.length, 2);
			assert.equal(parents[0].name, 'xpto1');
			assert.equal(parents[1].name, 'xpto2');
			t.start();
		});
	});

	t.asyncTest("destroy", function(assert) {
		expect(2);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp.destroy(parent).done(function(retrievedId) {
			t.equal(parent.id, retrievedId);

		}).then(function() {
			yawp(parent).fetch().fail(function(error) {
				if (t.isPhantomJS()) {
					assert.equal(error.status, 0);
				} else {
					assert.equal(error.status, 404);
				}
				t.start();
			});
		});
	});

})(QUnit, yawp, yawp.fixtures);
