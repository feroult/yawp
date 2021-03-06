(function (t, yawp, fx) {

    t.moduledef('child query', {
        testStart: function (start) {
            fx.reset().then(function () {
                fx.parent('parent1', {
                    name: 'xpto1'
                });

                fx.child('child1', {
                    name: 'xpto1',
                    parentId: fx.parent.parent1.id
                });

                fx.child('child2', {
                    name: 'xpto2',
                    parentId: fx.parent.parent1.id
                });

                fx.child('child3', {
                    name: 'xpto3',
                    parentId: fx.parent.parent1.id
                });

                fx.parent('parent2', {
                    name: 'xpto2'
                });

                fx.child('child4', {
                    name: 'xpto1',
                    parentId: fx.parent.parent2.id
                });

                fx.parent('nameless', {});

                fx.load(start);
            });
        }
    });

    t.asyncTest('where', function (assert) {
        expect(2);

        var parent1 = fx.parent.parent1;

        var where = ['name', '=', 'xpto1'];

        yawp('/children').from(parent1).where(where).list(function (children) {
            assert.equal(children.length, 1);
            assert.equal(children[0].name, 'xpto1');
            t.start();
        });
    });

    t.asyncTest('complex where', function (assert) {
        expect(3);

        var parent1 = fx.parent.parent1;
        var where = {
            op: 'or',
            c: [{
                p: 'name',
                op: '=',
                v: 'xpto1'
            }, {
                p: 'name',
                op: '=',
                v: 'xpto2'
            }]
        };

        yawp('/children').from(parent1).where(where).list(function (children) {
            assert.equal(children.length, 2);
            var names = [children[0].name, children[1].name].sort();
            assert.equal(names[0], 'xpto1');
            assert.equal(names[1], 'xpto2');
            t.start();
        });
    });

    t.asyncTest('where with null', function (assert) {
        expect(1);

        yawp('/parents').where(['name', '=', null]).list(function (parents) {
            assert.equal(parents.length, 1);
            t.start();
        });
    });

    t.asyncTest('where in', function (assert) {
        expect(3);

        var parent1 = fx.parent.parent1;

        var where = ['name', 'in', ['xpto1', 'xpto2']];

        yawp('/children').from(parent1).where(where).list(function (children) {
            assert.equal(children.length, 2);
            var names = [children[0].name, children[1].name].sort();
            assert.equal(names[0], 'xpto1');
            assert.equal(names[1], 'xpto2');
            t.start();
        });
    });

    t.asyncTest('global query', function (assert) {
        expect(9);

        var parent1 = fx.parent.parent1;
        var parent2 = fx.parent.parent2;

        var order = [{
            p: 'name'
        }];

        yawp('/children').order(order).list(function (children) {
            assert.equal(children.length, 4);

            assert.equal(children[0].name, 'xpto1');
            assert.equal(children[1].name, 'xpto1');
            assert.equal(children[2].name, 'xpto2');
            assert.equal(children[3].name, 'xpto3');

            assert.ok(children[0].parentId == parent1.id || children[0].parentId == parent2.id);
            assert.ok(children[1].parentId == parent1.id || children[1].parentId == parent2.id);
            assert.equal(children[2].parentId, parent1.id);
            assert.equal(children[3].parentId, parent1.id);
            t.start();
        });
    });

    t.asyncTest('limit', function (assert) {
        expect(3);

        var parent1 = fx.parent.parent1;

        var order = [{
            p: 'name'
        }];

        yawp('/children').from(parent1).order(order).limit(2).list(function (children) {
            assert.equal(children.length, 2);
            assert.equal(children[0].name, 'xpto1');
            assert.equal(children[1].name, 'xpto2');
            t.start();
        });
    });

    t.asyncTest('first', function (assert) {
        expect(1);

        var parent1 = fx.parent.parent1;

        var order = [{
            p: 'name'
        }];

        yawp('/children').from(parent1).order(order).first(function (child) {
            assert.equal(child.name, 'xpto1');
            t.start();
        });
    });

    t.asyncTest('only', function (assert) {
        expect(1);

        var parent1 = fx.parent.parent1;

        var where = ['name', '=', 'xpto1'];

        yawp('/children').from(parent1).where(where).only().then(function (child) {
            assert.equal(child.name, 'xpto1');
            t.start();
        });
    });

    t.asyncTest("sort", function (assert) {
        expect(4);

        var parent1 = fx.parent.parent1;

        var sort = [{
            p: 'name'
        }];

        yawp('/children').from(parent1).sort(sort).transform('simple').list(function (children) {
            assert.equal(children.length, 3);
            assert.equal(children[0].name, 'transformed xpto1');
            assert.equal(children[1].name, 'transformed xpto2');
            assert.equal(children[2].name, 'transformed xpto3');
            t.start();
        });

    });

})(QUnit, yawp, yawp.fixtures);
