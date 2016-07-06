(function (t, yawp, fx) {

    t.moduledef('parent query', {
        testStart: function (start) {
            fx.reset().then(function () {
                fx.parent('xpto1', {
                    name: 'xpto1'
                });

                fx.parent('xpto2', {
                    name: 'xpto2'
                });

                fx.load(start);
            });
        }
    });

    t.asyncTest('where', function (assert) {
        expect(2);

        var where = ['name', '=', 'xpto1'];

        yawp('/parents').where(where).list(function (parents) {
            assert.equal(parents.length, 1);
            assert.equal(parents[0].name, 'xpto1');
            t.start();
        });
    });

    t.asyncTest('order', function (assert) {
        expect(3);

        var order = [{
            p: 'name',
            d: 'desc'
        }];

        yawp('/parents').order(order).list(function (parents) {
            assert.equal(parents.length, 2);
            assert.equal(parents[0].name, 'xpto2');
            assert.equal(parents[1].name, 'xpto1');
            t.start();
        });
    });

    t.asyncTest('sort', function (assert) {
        expect(3);

        var sort = [{
            p: 'name',
            d: 'desc'
        }];

        yawp('/parents').sort(sort).list(function (parents) {
            assert.equal(parents.length, 2);
            assert.equal(parents[0].name, 'xpto2');
            assert.equal(parents[1].name, 'xpto1');
            t.start();
        });
    });

})(QUnit, yawp, yawp.fixtures);
