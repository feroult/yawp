(function (t, yawp, fx) {

    t.moduledef('pipes', {
        testStart: function (start) {
            fx.reset().then(start);
        }
    });

    function randomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1) + min);
    }

    t.asyncTest('parallel changes', function (assert) {

        expect(3);

        function skip() {
            t.ok(1);
            t.ok(1);
            t.ok(1);
            t.start();
        }

        function saveObject(id, group, callback) {
            var object = {
                id: '/piped_objects/' + id,
                group: group,
                counterId: '/piped_object_counters/1'
            };

            yawp('/piped_objects').create(object).then(function () {
                callback();
            }).catch(function () {
                callback();
            });
        }

        function destroyObject(id, callback) {
            var objectId = '/piped_objects/' + id;

            yawp(objectId).destroy().then(function () {
                callback();
            }).catch(function () {
                callback()
            });
        }

        function saveOrDestroyObject(id, group, callback) {

            var save = randomInt(0, 1);

            if (save) {
                saveObject(id, group, callback);
            } else {
                destroyObject(id, callback);
            }
        }

        const groups = ['group-a', 'group-b'];
        const MAX = 100;

        var count = 0;

        function saveObjectsInParallel(callback) {
            for (var i = 0; i < MAX; i++) {
                setTimeout(function () {
                    saveOrDestroyObject(randomInt(1, 10), groups[randomInt(0, 1)], function () {
                        count++;
                        if (count >= MAX) {
                            callback();
                        }
                    });
                }, i * randomInt(100, 200));
            }
        }

        const MAX_RETRIES = 15;

        var retries = 0;

        function assertCounter() {
            yawp('/piped_object_counters/1').fetch(function (counter) {
                if (counter.count != 10 || counter.countGroupA != 6 || counter.countGroupB != 4) {
                    if (retries >= MAX_RETRIES) {
                        t.start();
                        return;
                    }
                    retries++;
                    setTimeout(assertCounter, 1000);
                    return;
                }

                assert.equal(counter.count, 10);
                assert.equal(counter.countGroupA, 6);
                assert.equal(counter.countGroupB, 4);
                t.start();
            }).catch(function () {
                if (retries >= MAX_RETRIES) {
                    t.start();
                    return;
                }
                retries++;
                setTimeout(assertCounter, 1000);
                return
            });
        }


        function organizeObjects(callback) {
            saveObject(1, 'group-a', function () {
                saveObject(2, 'group-b', function () {
                    saveObject(3, 'group-a', function () {
                        saveObject(4, 'group-b', function () {
                            saveObject(5, 'group-a', function () {
                                saveObject(6, 'group-a', function () {
                                    saveObject(7, 'group-b', function () {
                                        saveObject(8, 'group-a', function () {
                                            saveObject(9, 'group-a', function () {
                                                saveObject(10, 'group-b', function () {
                                                    callback();
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        }

        yawp().fetch(function (welcome) {
            if (welcome.driver == "postgresql") {
                skip();
                return;
            }

            saveObjectsInParallel(function () {
                organizeObjects(function () {
                    assertCounter();
                });
            });
        });
    });

})(QUnit, yawp, yawp.fixtures);
