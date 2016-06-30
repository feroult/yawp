import request from './request';

var yawp = require('./../commons/yawp')(request);
var fixtures = require('./../commons/fixtures')(request);

yawp.fixtures = fixtures;

export default yawp;
