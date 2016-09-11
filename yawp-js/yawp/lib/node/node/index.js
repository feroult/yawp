'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.fx = undefined;

var _request = require('./request');

var _request2 = _interopRequireDefault(_request);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var yawp = require('./../commons/yawp')(_request2.default);
var fx = require('./../commons/fixtures')(_request2.default);

yawp.fixtures = fx;

exports.default = yawp;
exports.fx = fx;
//# sourceMappingURL=index.js.map