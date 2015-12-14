(function(t, yawp, fx) {

	t.moduledef('parent custom action', {
		testStart : function() {
			fx.reset();
		}
	});

	t.asyncTest("over object", function(assert) {
		expect(1);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp(parent).put('touched').done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'touched xpto');
			t.start();
		});
	});

	t.asyncTest("over object with params", function(assert) {
		expect(1);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp(parent).params({ 'arg' : 'myArg' }).put('touchedParams').done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'touched xpto by myArg');
			t.start();
		}).fail(function (e) {
			console.log(e);
			assert.fail();
		});
	});

	t.asyncTest("over collection", function(assert) {
		expect(3);

		fx.parent('parent1', {
			name : 'xpto1'
		});

		fx.parent('parent2', {
			name : 'xpto2'
		});

		yawp('/parents').put('touched').done(function(parents) {
			assert.equal(parents.length, 2);
			assert.equal(parents[0].name, 'touched xpto1');
			assert.equal(parents[1].name, 'touched xpto2');
			t.start();
		});
	});

    t.asyncTest("all http verbs", function(assert) {
        expect(5);

        fx.parent('parent', {
            id: '/parents/1',
            name : 'xpto1'
        });

        yawp('/parents/1').get('all-http-verbs').done(function(ok) {
            assert.equal(ok, "ok");
            yawp('/parents/1').post('all-http-verbs').done(function(ok) {
                assert.equal(ok, "ok");
                yawp('/parents/1').put('all-http-verbs').done(function(ok) {
                    assert.equal(ok, "ok");
                    yawp('/parents/1')._patch('all-http-verbs').done(function(ok) {
                        assert.equal(ok, "ok");
                        yawp('/parents/1')._delete('all-http-verbs').done(function(ok) {
                            assert.equal(ok, "ok");
                            t.start();
                        });
                    });
                });
            });
        });
    });

    t.asyncTest("over object with json string", function(assert) {
        expect(1);

        var parent = fx.parent('parent', {
            name : 'xpto'
        });

        var json = { stringValue: 'json string' };

        yawp(parent).json(json).post('with-json-string').done(function(result) {
            assert.equal(result, 'json string');
            t.start();
        });
    });


    t.asyncTest("over object with json object", function(assert) {
        expect(1);

        var parent = fx.parent('parent', {
            name : 'xpto'
        });

        var json = { stringValue: 'json object' };

        yawp(parent).json(json).post('with-json-object').done(function(result) {
            assert.equal(result, 'json object');
            t.start();
        });
    });

    t.asyncTest("over collection with json object", function(assert) {
        expect(1);

        var json = { stringValue: 'json object' };

        yawp('/parents').json(json).post('collection-with-json-object').done(function(result) {
            assert.equal(result, 'json object');
            t.start();
        });
    });

    t.asyncTest("over object with json list", function(assert) {
        expect(1);

        var parent = fx.parent('parent', {
            name : 'xpto'
        });

        var json = [ { stringValue: 'pojo' }, { stringValue: 'list' } ];

        yawp(parent).json(json).post('with-json-list').done(function(result) {
            assert.equal(result, 'pojo list');
            t.start();
        });
    });

    t.asyncTest("error response", function(assert) {
        expect(1);

        yawp('/parents').put('with-exception').error(function(err) {
            var result = err.responseJSON;
            assert.equal(result.title, 'sample json exception body');
            t.start();
        });
    });

})(QUnit, yawp, yawp.fixtures);