if(typeof self !== 'undefined') {
    require('es6-promise').polyfill();
    require('isomorphic-fetch');
}

export function extend() {
    var result = arguments[0] || {};

    for (var i = 1, l = arguments.length; i < l; i++) {
        var obj = arguments[i];
        for (var attrname in obj) {
            result[attrname] = obj[attrname];
        }
    }

    return result;
}

export function baseAjax(type, options) {
    var fail,
        done,
        exception,
        then,
        error;

    var callbacks = {
        fail: function (callback) {
            fail = callback;
            return callbacks;
        },
        done: function (callback) {
            done = callback;
            return callbacks;
        },
        exception: function (callback) {
            exception = callback;
            return callbacks;
        },
        then: function (callback) {
            then = callback;
            return callbacks;
        },
        error: function (callback) {
            error = callback;
            return callbacks;
        }
    };

    var url = options.url + (options.query ? '?' + toUrlParam(options.query) : '');

    var headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
    };

    fetch(url, {
        method: type,
        headers: headers,
        body: options.data
    }).then((response) => response.json())
        .then((response) => {
            done && done(response);
            then && then(response);
        })
        .catch((error) => {
            fail && fail(error);
            error && error(error);
            exception && exception(error);
        });

    return callbacks;
}

function toUrlParam(jsonParams) {
    return Object.keys(jsonParams).map(function (k) {
        return encodeURIComponent(k) + '=' + encodeURIComponent(jsonParams[k]);
    }).join('&');
}
