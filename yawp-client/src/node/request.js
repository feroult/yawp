import { extend, toUrlParam } from '../commons/utils';

if (typeof fetch === 'undefined') {
    var fetch = require('node-fetch');
}

export default function request(url, query, options) {
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
        'Content-Type': 'application/json;charset=UTF-8',
    });

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
