Object.defineProperty(exports, "__esModule", {
    value: true
});


function node() {
    var yawp = require('./lib/node/node');
    return yawp.fx;
}

function web() {
    return require('./lib/web/yawp.fixtures.min').default;
}

var fx = typeof window === 'undefined' ? node() : web();

exports.default = fx;
