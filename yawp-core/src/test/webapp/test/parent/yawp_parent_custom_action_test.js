(function (t, yawp, fx) {

    t.moduledef('parent custom action', {
        testStart: function (start) {
            fx.reset().then(start);
        }
    });

    t.asyncTest("over object", function (assert) {
        expect(1);

        fx.parent('parent', {
            name: 'xpto'
        });

        fx.load(function () {
            var parent = fx.parent.parent;

            yawp(parent).put('touched').then(function (retrievedParent) {
                assert.equal(retrievedParent.name, 'touched xpto');
                t.start();
            });
        });
    });

    t.asyncTest("over object with params", function (assert) {
        expect(1);

        fx.parent('parent', {
            name: 'xpto'
        });


        fx.load(function () {
            var parent = fx.parent.parent;

            yawp(parent).params({'arg': 'myArg'}).put('touchedParams').then(function (retrievedParent) {
                assert.equal(retrievedParent.name, 'touched xpto by myArg');
                t.start();
            }).catch(function (e) {
                console.log(e);
                assert.fail();
            });
        });
    });

    t.asyncTest("over collection", function (assert) {
        expect(3);

        fx.parent('parent1', {
            name: 'xpto1'
        });

        fx.parent('parent2', {
            name: 'xpto2'
        });

        fx.load(function () {
            yawp('/parents').put('touched').then(function (parents) {
                assert.equal(parents.length, 2);
                assert.equal(parents[0].name, 'touched xpto1');
                assert.equal(parents[1].name, 'touched xpto2');
                t.start();
            });
        });

    });

    t.asyncTest("all http verbs", function (assert) {
        expect(5);

        fx.parent('parent', {
            id: '/parents/1',
            name: 'xpto1'
        });

        fx.load(function () {
            yawp('/parents/1').get('all-http-verbs').then(function (ok) {
                assert.equal(ok, "ok");
                yawp('/parents/1').post('all-http-verbs').then(function (ok) {
                    assert.equal(ok, "ok");
                    yawp('/parents/1').put('all-http-verbs').then(function (ok) {
                        assert.equal(ok, "ok");
                        yawp('/parents/1')._patch('all-http-verbs').then(function (ok) {
                            assert.equal(ok, "ok");
                            yawp('/parents/1')._delete('all-http-verbs').then(function (ok) {
                                assert.equal(ok, "ok");
                                t.start();
                            });
                        });
                    });
                });
            });
        });
    });

    t.asyncTest("over object with json string", function (assert) {
        expect(1);

        fx.parent('parent', {
            name: 'xpto'
        });

        fx.load(function () {
            var parent = fx.parent.parent;
            var json = {stringValue: 'json string'};

            yawp(parent).json(json).post('with-json-string').then(function (result) {
                assert.equal(result, 'json string');
                t.start();
            });
        });
    });


    t.asyncTest("over object with json object", function (assert) {
        expect(1);

        fx.parent('parent', {
            name: 'xpto'
        });

        fx.load(function () {
            var parent = fx.parent.parent;
            var json = {stringValue: 'json object'};

            yawp(parent).json(json).post('with-json-object').then(function (result) {
                assert.equal(result, 'json object');
                t.start();
            });
        });
    });

    t.asyncTest("over collection with json object", function (assert) {
        expect(1);

        var json = {stringValue: 'json object'};

        yawp('/parents').json(json).post('collection-with-json-object').then(function (result) {
            assert.equal(result, 'json object');
            t.start();
        });
    });

    t.asyncTest("over object with json list", function (assert) {
        expect(1);

        fx.parent('parent', {
            name: 'xpto'
        });

        fx.load(function () {
            var parent = fx.parent.parent;
            var json = [{stringValue: 'pojo'}, {stringValue: 'list'}];

            yawp(parent).json(json).post('with-json-list').then(function (result) {
                assert.equal(result, 'pojo list');
                t.start();
            });
        });
    });

    t.asyncTest("error response", function (assert) {
        expect(1);

        yawp('/parents').put('with-exception').catch(function (err) {
            var result = JSON.parse(err.responseText);
            assert.equal(result.title, 'sample json exception body');
            t.start();
        });
    });

    t.asyncTest("action with void return type", function (assert) {
        expect(3);

        yawp('/parents').post('with-void-return').then(function (result) {
            assert.ok(result);
            yawp('/parents').put('with-void-return').then(function (result) {
                assert.ok(result);
                yawp('/parents').get('with-void-return').then(function (result) {
                    assert.ok(result);
                    t.start();
                });
            });
        });
    });


    t.asyncTest("check if fixtures servlet disable shields", function (assert) {
        expect(1);

        yawp.config(function (c) {
            c.baseUrl('/fixtures');
        });

        yawp('/parents').get('check-if-fixtures-servlet-disable-shields').then(function (result) {
            assert.equal(result, "xpto");
            yawp.config(function (c) {
                c.baseUrl('/api');
            });

            t.start();
        });
    });

})(QUnit, yawp, yawp.fixtures);