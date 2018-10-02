'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _keys = require('babel-runtime/core-js/object/keys');

var _keys2 = _interopRequireDefault(_keys);

exports.extend = extend;
exports.toUrlParam = toUrlParam;

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

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
    return (0, _keys2.default)(jsonParams).map(function (k) {
        return encodeURIComponent(k) + '=' + encodeURIComponent(jsonParams[k]);
    }).join('&');
}
//# sourceMappingURL=utils.js.map