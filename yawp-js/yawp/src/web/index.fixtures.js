import request from './request';

var fx = require('./../commons/fixtures')(request);

if ('yawp' in window) {
    window.yawp.fixtures = fx;
}

export default fx;
