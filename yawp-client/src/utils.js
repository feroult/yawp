//require('babel-polyfill');

if (typeof self !== 'undefined') {
    //require('es6-promise').polyfill();
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

export function baseAjax(url, query, options) {
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

    url += (query ? '?' + toUrlParam(options.query) : '');

    options.headers = options.headers || {};
    extend(options.headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
    });

    console.log('url', url);
    console.log('options', options);

    //(async function () {
    //    let response = await fetch(url, options);
    //    let json = await response.json();
    //    done && done(json);
    //})();

    fetch(url, options).then((response) => response.json())
        .then((response) => {
            done && done(response);
            then && then(response);
        })
        .catch((err) => {
            fail && fail(err);
            error && error(err);
            exception && exception(err);
        });

    return callbacks;
}

function toUrlParam(jsonParams) {
    return Object.keys(jsonParams).map(function (k) {
        return encodeURIComponent(k) + '=' + encodeURIComponent(jsonParams[k]);
    }).join('&');
}
