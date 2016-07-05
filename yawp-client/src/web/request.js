import { extend, toUrlParam } from '../commons/utils';

export default function (url, options) {
    return new Promise((resolve, reject) => {
        var query = options.query;
        delete options.query;

        var request = new XMLHttpRequest();

        request.onreadystatechange = function () {
            resolveOrReject(request, options, resolve, reject);
        };

        request.open(options.method, url + (query ? '?' + toUrlParam(query) : ''));
        setHeaders(request, options.headers);
        request.send(options.body);
    });
}

function resolveOrReject(request, options, resolve, reject) {
    if (request.readyState === 4) {
        if (request.status === 200) {
            resolve(options.json ? JSON.parse(request.responseText) : request.responseText);
        } else {
            reject(request);
        }
    }
}

function setHeaders(request, headers) {
    headers = headers || {};
    extend(headers, {
        'Accept': 'application/json',
        'Content-Type': 'application/json;charset=UTF-8',
    });
    for (var key in headers) {
        if (headers.hasOwnProperty(key)) {
            request.setRequestHeader(key, headers[key]);
        }
    }
}
