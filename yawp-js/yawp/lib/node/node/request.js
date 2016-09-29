'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.default = request;

var _utils = require('../commons/utils');

var nodeFetch;

if (typeof fetch === 'undefined') {
    nodeFetch = eval("require('node-fetch')");
} else {
    nodeFetch = fetch;
}

function request(url, options) {
    var query = options.query;
    delete options.query;

    url += query ? '?' + (0, _utils.toUrlParam)(query) : '';

    //console.log('request', url, options);

    if (!options.json) {
        return nodeFetch(url, options);
    }
    return jsonRequest(options, url);
}

function jsonRequest(options, url) {
    options.headers = options.headers || {};
    (0, _utils.extend)(options.headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
    });
    return nodeFetch(url, options).then(function (response) {
        return response.json();
    });
}
module.exports = exports['default'];
//# sourceMappingURL=request.js.map