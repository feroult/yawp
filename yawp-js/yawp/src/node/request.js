import { extend, toUrlParam } from '../commons/utils';

var nodeFetch;

if (typeof fetch === 'undefined') {
    nodeFetch = eval("require('node-fetch')");
} else {
    nodeFetch = fetch;
}

export default function request(url, options) {
    var query = options.query;
    delete options.query;

    url += (query ? '?' + toUrlParam(query) : '');

    //console.log('request', url, options);

    if (!options.json) {
        return nodeFetch(url, options);
    }
    return jsonRequest(options, url);
}

function jsonRequest(options, url) {
    options.headers = options.headers || {};
    extend(options.headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json;charset=UTF-8',
    });
    return nodeFetch(url, options).then((response) =>response.json());
}
