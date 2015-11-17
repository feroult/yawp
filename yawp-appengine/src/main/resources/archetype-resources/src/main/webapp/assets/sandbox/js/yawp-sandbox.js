(function ($) {
    function parseLog(args) {
        var message = '';
        for (var i = 0, l = args.length; i < l; i++) {
            var value = args[i];
            if (i > 0) {
                message += ' ';
            }
            if (typeof value === 'object') {
                message += JSON.stringify(value, null, 2);
            } else {
                message += value;
            }
        }
        return message;
    }

    function writeLog(message) {
        var code = $('#log pre code');
        code.append(message + '\n');
        code.append('<hr>');
        hljs.highlightBlock(code[0]);
        var pre = $('#log pre')[0];
        pre.scrollTop = pre.scrollHeight;
    }

    function hookLog() {
        if (typeof console != "undefined") {
            if (typeof console.log != 'undefined') {
                console.olog = console.log;
            } else {
                console.olog = function () {};
            }
        }

        console.log = function () {
            var message = parseLog(arguments);
            console.olog(message);
            writeLog(message);
        };

        console.error = console.debug = console.info = console.log
    }

    function sandboxInit() {
        var sanbox = new Sandbox.View({
            el: $('#sandbox'),
            model: new Sandbox.Model({
                iframe: false,
                fallback: true
            })
        });

        $('#prompt').focus();
    }

    jQuery(document).ready(function ($) {
        sandboxInit();

        yawp.config(function (c) {
            c.baseUrl('http://localhost:8080/api');
        });

        hookLog();
        hljs.initHighlightingOnLoad();
    });

})(jQuery);