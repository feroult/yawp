function node() {
    return require('./lib/node/node').default;
}

function web() {
    return require('./lib/web/yawp.min').default;
}

var yawp = typeof window === 'undefined' ? node() : web();
var fx = yawp.fixtures;

export default yawp;
export { fx };
