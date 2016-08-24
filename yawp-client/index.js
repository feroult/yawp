function node() {
    return require('./lib/node/node');
}

function web() {
    return require('./lib/web/yawp.min');
}

var lib = typeof window === 'undefined' ? node() : web();

var yawp = lib.default || lib;

module.exports = yawp;