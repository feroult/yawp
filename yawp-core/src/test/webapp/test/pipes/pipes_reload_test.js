(function (t, yawp, fx, $) {

    t.moduledef('pipes', {
        testStart: function () {
            fx.reset();
        }
    });

    function skip() {
        t.ok(1);
        t.ok(1);
        t.start();
    }

    t.asyncTest('reload test', function (assert) {

        expect(2);

        function saveObject(id, group, callback) {
            var object = {
                id: '/piped_objects/' + id,
                group: group,
                counterId: '/piped_object_counters/1'
            };

            yawp('/piped_objects').create(object).done(function () {
                callback();
            }).fail(function () {
                callback()
            });
        }

        function saveObjects(callback) {
            saveObject(1, "xpto", function () {
                saveObject(2, "xpto", function () {
                    saveObject(3, "xpto", callback);
                });
            });
        }

        const MAX_RETRIES = 15;

        var retries;

        function assertCounter(callback) {
            yawp('/piped_object_counters/1').fetch(function (counter) {
                if (counter.count != 3) {
                    if (retries >= MAX_RETRIES) {
                        t.start();
                        return;
                    }
                    retries++;
                    setTimeout(function () {
                        assertCounter(callback);
                    }, 1000);
                    return;
                }

                assert.equal(counter.count, 3);
                callback();

            }).fail(function () {
                if (retries >= MAX_RETRIES) {
                    t.start();
                    return;
                }
                retries++;
                setTimeout(function () {
                    assertCounter(callback);
                }, 1000);
                return
            });
        }

        function reloadPipe(callback) {
            var counter = {
                id: '/piped_object_counters/1',
                count: 0
            };

            yawp.patch(counter).done(function () {
                $.get('/_ah/yawp/pipes/reload?pipe=io.yawp.repository.pipes.CounterPipe', callback);
            });
        }

        yawp().fetch(function (welcome) {
            if (welcome.driver == "postgresql") {
                skip();
                return;
            }

            saveObjects(function () {
                retries = 0;
                assertCounter(function () {
                    reloadPipe(function () {
                        retries = 0;
                        assertCounter(function () {
                            t.start();
                        });
                    })
                });
            });
        });
    });

})(QUnit, yawp, yawp.fixtures, jQuery);
