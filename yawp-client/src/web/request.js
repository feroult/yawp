import { extend, toUrlParam } from '../commons/utils';

export default function (url, query, options) {
    var fail
        , done
        , exception
        , then
        , error
        , request;

    url += (query ? '?' + toUrlParam(query) : '');

    var callbacks = {
        fail: function (callback) {
            fail = callback;
            if (!options.async) {
                request.onreadystatechange();
            }
            return callbacks;
        },
        done: function (callback) {
            done = callback;
            if (!options.async) {
                request.onreadystatechange();
            }
            return callbacks;
        },
        exception: function (callback) {
            exception = callback;
            if (!options.async) {
                request.onreadystatechange();
            }
            return callbacks;
        },
        then: function (callback) {
            then = callback;
            if (!options.async) {
                request.onreadystatechange();
            }
            return callbacks;
        },
        error: function (callback) {
            error = callback;
            if (!options.async) {
                request.onreadystatechange();
            }
            return callbacks;
        }
    };

    request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (request.readyState === 4) {
            if (request.status === 200) {
                if (done) {
                    done(options.json ? JSON.parse(request.responseText) : request.responseText);
                }
                if (then) {
                    then(options.json ? JSON.parse(request.responseText) : request.responseText);
                }
            } else {
                if (fail) {
                    fail(request);
                }
                if (error) {
                    error(extend({}, request, {responseJSON: JSON.parse(request.responseText)}));
                }
                if (exception) {
                    exception(options.json ? JSON.parse(request.responseText) : request.responseText);
                }
            }
        }
    };

    request.open(options.method, url, options.async);
    setHeaders(request, options.headers);
    request.send(options.body);

    return callbacks;
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
