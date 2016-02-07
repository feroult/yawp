(function (t, yawp, fx) {

    t.moduledef('pipes', {
        testStart: function () {
            fx.reset();
        }
    });

    function randomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1) + min);
    }

    t.asyncTest('parallel changes', function (assert) {
        expect(3);

        function saveObject(id, group, callback) {
            var object = {
                id: '/basic_objects/' + id,
                stringValue: group
            };

            yawp('/basic_objects').create(object).done(function () {
                callback();
            });
        }

        const groups = ['group-a', 'group-b'];
        const MAX = 10;
        var count = 0;

        function saveObjectsInParallel(callback) {
            for (var i = 0; i < MAX; i++) {
                setTimeout(function () {
                    saveObject(randomInt(1, 3), groups[randomInt(0, 1)], function () {
                        count++;
                        if (count >= MAX) {
                            callback();
                        }
                    });
                }, i * randomInt(1, 50));
            }
        }

        function assertCounter() {
            yawp('/basic_objects_counter/1').fetch(function (counter) {
                assert.equal(counter.count, 3);
                assert.equal(counter.countGroupA, 2);
                assert.equal(counter.countGroupB, 1);
                t.start();
            }).fail(function (response) {
                if (response.status == 404) {
                    setTimeout(assertCounter, 1000);
                    return;
                }
                t.start();
            });
        }

        function organizeObjects(callback) {
            saveObject(1, 'group-a', function () {
                saveObject(2, 'group-b', function () {
                    saveObject(3, 'group-a', function () {
                        callback();
                    });
                });
            });
        }

        saveObjectsInParallel(function () {
            organizeObjects(function () {
                assertCounter();
            });
        });
    });


})(QUnit, yawp, yawp.fixtures);
