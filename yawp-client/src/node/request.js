import { extend, toUrlParam } from '../commons/utils';

if (typeof fetch === 'undefined') {
    var fetch = require('node-fetch');
}

export default function request(url, query, options) {
    url += (query ? '?' + toUrlParam(options.query) : '');

    if (!options.json) {
        return fetch(url, options);
    }

    options.headers = options.headers || {};
    extend(options.headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json;charset=UTF-8',
    });

    return fetch(url, options).then((response) => response.json());
}
