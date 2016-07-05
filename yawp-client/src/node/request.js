import { extend, toUrlParam } from '../commons/utils';

if (typeof fetch === 'undefined') {
    var fetch = require('node-fetch');
}

export default function request(url, options) {
    var query = options.query;
    delete options.query;

    url += (query ? '?' + toUrlParam(query) : '');

    if (!options.json) {
        return fetch(url, options);
    }
    return jsonRequest(options, url);
}

function jsonRequest(options, url) {
    options.headers = options.headers || {};
    extend(options.headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json;charset=UTF-8',
    });
    return fetch(url, options).then((response) => response.json());
}
