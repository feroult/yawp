'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.default = request;

var _utils = require('../commons/utils');

if (typeof fetch === 'undefined') {
    var fetch = require('node-fetch');
}

function request(url, query, options) {
    var _fail, _done, _exception, _then, _error;

    var callbacks = {
        fail: function fail(callback) {
            _fail = callback;
            return callbacks;
        },
        done: function done(callback) {
            _done = callback;
            return callbacks;
        },
        exception: function exception(callback) {
            _exception = callback;
            return callbacks;
        },
        then: function then(callback) {
            _then = callback;
            return callbacks;
        },
        error: function error(callback) {
            _error = callback;
            return callbacks;
        }
    };

    url += query ? '?' + (0, _utils.toUrlParam)(options.query) : '';

    options.headers = options.headers || {};
    (0, _utils.extend)(options.headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
    });

    //(async function () {
    //    let response = await fetch(url, options);
    //    let json = await response.json();
    //    done && done(json);
    //})();

    fetch(url, options).then(function (response) {
        return response.json();
    }).then(function (response) {
        _done && _done(response);
        _then && _then(response);
    }).catch(function (err) {
        _fail && _fail(err);
        _error && _error(err);
        _exception && _exception(err);
    });

    return callbacks;
}
module.exports = exports['default'];