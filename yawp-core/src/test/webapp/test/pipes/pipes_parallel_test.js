(function (t, yawp, fx) {

    t.moduledef('pipes', {
        testStart: function () {
            fx.reset();
        }
    });

    function randomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1) + min);
    }

    t.skip('parallel changes', function (assert) {
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
        const MAX = 20;
        var count = 0;

        function saveObjectsInParallel(callback) {
            for (var i = 0; i < MAX; i++) {
                setTimeout(function () {
                    saveObject(randomInt(1, 1), groups[randomInt(0, 1)], function () {
                        count++;
                        if (count >= MAX) {
                            callback();
                        }
                    });
                }, i * randomInt(1, 20));
            }
        }

        function assertCounter() {
            yawp('/basic_objects_counter/1').fetch(function (counter) {
                assert.equal(counter.count, 3);
                assert.equal(counter.countGroupA, 1);
                assert.equal(counter.countGroupB, 2);
                t.start();
            });
        }

        function organizeObjects() {
            saveObject(1, 'group-a', function () {
                saveObject(2, 'group-b', function () {
                    saveObject(3, 'group-b', function () {
                        assertCounter();
                    });
                });
            });
        }

        saveObjectsInParallel(organizeObjects);
    });


})(QUnit, yawp, yawp.fixtures);
