(function (t) {

    yawp.fixtures.config(function (c) {
        c.baseUrl('/fixtures');
        c.resetUrl('/_ah/yawp/datastore/delete_all');

        c.bind('parent', '/parents');
        c.bind('job', '/jobs');
        c.bind('child', '/children', 'parentId');
    });

    function moduledef(module, options) {
        t.module(module);
        if (options.testStart) {
            t.testStart(function (details) {
                if (details.module != module) {
                    return;
                }
                t.stop();
                options.testStart(t.start);
            });
        }
    }

    function skip() {

    }

    t.moduledef = moduledef;
    t.skip = skip;

    t.isPhantomJS = function () {
        return navigator.userAgent.indexOf("PhantomJS") > 0;
    };

})(QUnit, yawp, yawp.fixtures);
