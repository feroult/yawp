'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.extend = extend;
exports.toUrlParam = toUrlParam;
function extend() {
    var result = arguments[0] || {};

    for (var i = 1, l = arguments.length; i < l; i++) {
        var obj = arguments[i];
        for (var attrname in obj) {
            result[attrname] = obj[attrname];
        }
    }

    return result;
}

function toUrlParam(jsonParams) {
    return Object.keys(jsonParams).map(function (k) {
        return encodeURIComponent(k) + '=' + encodeURIComponent(jsonParams[k]);
    }).join('&');
}
//# sourceMappingURL=utils.js.map