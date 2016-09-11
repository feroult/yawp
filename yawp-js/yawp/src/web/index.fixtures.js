import request from './request';

var fx = require('./../commons/fixtures')(request);

if (yawp) {
    yawp.fixtures = fx;
}

export default fx;