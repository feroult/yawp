import request from './request';

var yawp = require('./../commons/yawp')(request);
var fx = require('./../commons/fixtures')(request);

yawp.fixtures = fx;

export default yawp;
export { fx };
