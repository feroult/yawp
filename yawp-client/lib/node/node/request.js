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
    url += query ? '?' + (0, _utils.toUrlParam)(options.query) : '';

    if (!options.json) {
        return fetch(url, options);
    }

    options.headers = options.headers || {};
    (0, _utils.extend)(options.headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
    });

    return fetch(url, options).then(function (response) {
        return response.json();
    });
}
module.exports = exports['default'];
//# sourceMappingURL=request.js.map