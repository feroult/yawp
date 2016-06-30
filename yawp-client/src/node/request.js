import { extend, toUrlParam } from '../commons/utils';

if (typeof self !== 'undefined') {
    require('es6-promise').polyfill();
    require('isomorphic-fetch');
}

async function lala() {
    var x = await y();
    return x;
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

    console.log('url', url);
    console.log('options', options);

    //(async function () {
    //    let response = await fetch(url, options);
    //    let json = await response.json();
    //    done && done(json);
    //})();

    var fetch2 = fetch(url, options);

    console.log('f', fetch2);

    fetch2.then((response) => response.json())
        .then((response) => {
            console.log('here 111');
            done && done(response);
            then && then(response);
        })
        .catch((err) => {
            fail && fail(err);
            error && error(err);
            exception && exception(err);
        });

    console.log('here 222');

    return callbacks;
}
