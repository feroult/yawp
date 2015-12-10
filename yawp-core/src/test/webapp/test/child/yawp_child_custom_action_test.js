(function(t, yawp, fx) {

	t.moduledef('child custom action', {
		testStart : function() {
			fx.reset();

			fx.parent('parent', {});
		}
	});

	t.asyncTest("over object", function(assert) {
		expect(1);

		var parent = fx.parent('parent');

		var child = fx.child('child', {
			name : 'xpto',
			parentId : parent.id
		});

		yawp(child).put('touched').done(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'touched xpto');
			t.start();
		});
	});

	t.asyncTest("over object with params", function(assert) {
		expect(1);

		var parent = fx.parent('parent');

		var child = fx.child('child', {
			name : 'xpto',
			parentId : parent.id
		});

		yawp(child).params({ 'arg' : 'myArg' }).put('touchedParams').done(function(retrievedChild) {
			assert.equal(retrievedChild.name, 'touched xpto by myArg');
			t.start();
		});
	});

	t.asyncTest("over collection", function(assert) {
		expect(3);

		var parent = fx.parent('parent');

		fx.child('child1', {
			name : 'xpto1',
			parentId : parent.id
		});

		fx.child('child2', {
			name : 'xpto2',
			parentId : parent.id
		});

		yawp('/children').from(parent).put('touched').done(function(children) {
			assert.equal(children.length, 2);
			assert.equal(children[0].name, 'touched xpto1');
			assert.equal(children[1].name, 'touched xpto2');
			t.start();
		});
	});


	t.asyncTest("over collection with json and params", function(assert) {
		expect(1);

		var parent = fx.parent('parent');

		var object = { id: '/basic_objects/1', stringValue:  'basic object' };

		var params = { x: 'y' };

		yawp('/children').from(parent).json(object).params(params).post('with-json-and-params').done(function(result) {
		    assert.equal(result, 'basic object y - /basic_objects/1');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);