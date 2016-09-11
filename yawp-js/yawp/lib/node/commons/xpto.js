'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});
function node() {
    return require('./lib/node/node');
}

function web() {
    return require('./lib/web/yawp.min');
}

var lib = typeof window === 'undefined' ? node() : web();

var yawp = lib.default;
var fx = lib.fx;

exports.default = yawp;
exports.fx = fx;
//# sourceMappingURL=xpto.js.map