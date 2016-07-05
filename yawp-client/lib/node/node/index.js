'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _request = require('./request');

var _request2 = _interopRequireDefault(_request);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var yawp = require('./../commons/yawp')(_request2.default);
var fixtures = require('./../commons/fixtures2')(_request2.default);

yawp.fixtures = fixtures;

exports.default = yawp;
module.exports = exports['default'];
//# sourceMappingURL=index.js.map