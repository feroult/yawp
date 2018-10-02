'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _get = function get(object, property, receiver) { if (object === null) object = Function.prototype; var desc = Object.getOwnPropertyDescriptor(object, property); if (desc === undefined) { var parent = Object.getPrototypeOf(object); if (parent === null) { return undefined; } else { return get(parent, property, receiver); } } else if ("value" in desc) { return desc.value; } else { var getter = desc.get; if (getter === undefined) { return undefined; } return getter.call(receiver); } };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _utils = require('./utils');

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var _baseUrl = '/api';
var _defaultFetchOptions = {};
var customFetchOptionsFn = null;
var _then = undefined;
var _catch2 = undefined;

function normalize(arg) {
    if (!arg) {
        return '';
    }
    if (arg instanceof Object) {
        return extractId(arg);
    }
    return arg;
}

function hasId(object) {
    return object.id;
}

function extractId(object) {
    if (hasId(object)) {
        return object.id;
    }
    throw 'use yawp(id) if your endpoint does not have a @Id field called id';
}

exports.default = function (request) {

    function yawpFn(baseArg) {

        var options = {};
        var q = {};

        var Yawp = function () {
            function Yawp(props) {
                _classCallCheck(this, Yawp);

                if (props) {
                    (0, _utils.extend)(this, props);
                }
            }

            // request

            _createClass(Yawp, [{
                key: 'save',


                // instance method

                value: function save(cb) {
                    var promise = this.createOrUpdate();
                    return cb ? promise.then(cb) : promise;
                }
            }, {
                key: 'createOrUpdate',
                value: function createOrUpdate() {
                    var _this = this;

                    var promise = void 0;
                    if (hasId(this)) {
                        options.url = this.id;
                        promise = this.constructor.update(this);
                    } else {
                        promise = this.constructor.create(this).then(function (object) {
                            _this.id = object.id;
                            return object;
                        });
                    }
                    return promise;
                }
            }, {
                key: 'destroy',
                value: function destroy(cb) {
                    var promise = this.constructor.destroy(this);
                    return cb ? promise.then(cb) : promise;
                }
            }, {
                key: 'get',
                value: function get(action) {
                    options.url = extractId(this);
                    return this.constructor.get(action);
                }
            }, {
                key: 'put',
                value: function put(action) {
                    options.url = extractId(this);
                    return this.constructor.put(action);
                }
            }, {
                key: '_patch',
                value: function _patch(action) {
                    options.url = extractId(this);
                    return this.constructor._patch(action);
                }
            }, {
                key: 'post',
                value: function post(action) {
                    options.url = extractId(this);
                    return this.constructor.post(action);
                }
            }, {
                key: '_delete',
                value: function _delete(action) {
                    options.url = extractId(this);
                    return this.constructor._delete(action);
                }
            }], [{
                key: 'clear',
                value: function clear() {
                    options = {
                        url: normalize(baseArg)
                    };
                    q = {};
                }
            }, {
                key: 'prepareRequestOptions',
                value: function prepareRequestOptions() {
                    var _options = (0, _utils.extend)({}, options);
                    this.clear();
                    return _options;
                }
            }, {
                key: 'baseRequest',
                value: function baseRequest(type) {
                    var options = this.prepareRequestOptions();

                    var url = _baseUrl + options.url;
                    delete options.url;

                    options.method = type;
                    options.json = true;
                    (0, _utils.extend)(options, _defaultFetchOptions);
                    customFetchOptionsFn && customFetchOptionsFn(options);

                    var req = request(url, options);

                    if (_then) {
                        req = req.then(_then);
                    }

                    if (_catch2) {
                        req = req.catch(_catch2);
                    }

                    return req;
                }
            }, {
                key: 'wrapInstance',
                value: function wrapInstance(object) {
                    return new this(object);
                }
            }, {
                key: 'wrapArray',
                value: function wrapArray(objects) {
                    var _this2 = this;

                    return objects.map(function (object) {
                        return _this2.wrapInstance(object);
                    });
                }
            }, {
                key: 'wrappedRequest',
                value: function wrappedRequest(type) {
                    var _this3 = this;

                    return this.baseRequest(type).then(function (result) {
                        if (Array === result.constructor) {
                            return _this3.wrapArray(result);
                        }
                        return _this3.wrapInstance(result);
                    });
                }

                // query

            }, {
                key: 'from',
                value: function from(parentBaseArg) {
                    var parentBase = normalize(parentBaseArg);
                    options.url = parentBase + options.url;
                    return this;
                }
            }, {
                key: 'transform',
                value: function transform(t) {
                    this.param('t', t);
                    return this;
                }
            }, {
                key: 'where',
                value: function where(data) {
                    if (arguments.length === 1) {
                        q.where = data;
                    } else {
                        q.where = [].slice.call(arguments);
                    }
                    return this;
                }
            }, {
                key: 'order',
                value: function order(data) {
                    q.order = data;
                    return this;
                }
            }, {
                key: 'sort',
                value: function sort(data) {
                    q.sort = data;
                    return this;
                }
            }, {
                key: 'limit',
                value: function limit(data) {
                    q.limit = data;
                    return this;
                }
            }, {
                key: 'fetch',
                value: function fetch(arg) {
                    var cb = typeof arg === 'function' ? arg : undefined;

                    if (arg && !cb) {
                        options.url += '/' + arg;
                    }

                    var promise = this.wrappedRequest('GET');

                    if (cb) {
                        return promise.then(cb);
                    }

                    return promise;
                }
            }, {
                key: 'setupQuery',
                value: function setupQuery() {
                    if (Object.keys(q).length > 0) {
                        this.param('q', JSON.stringify(q));
                    }
                }
            }, {
                key: 'list',
                value: function list(cb) {
                    this.setupQuery();

                    var promise = this.wrappedRequest('GET');

                    if (cb) {
                        return promise.then(cb);
                    }
                    return promise;
                }
            }, {
                key: 'first',
                value: function first(cb) {
                    this.limit(1);

                    return this.list(function (objects) {
                        var object = objects.length === 0 ? null : objects[0];
                        return cb ? cb(object) : object;
                    });
                }
            }, {
                key: 'only',
                value: function only(cb) {
                    return this.list(function (objects) {
                        if (objects.length !== 1) {
                            throw 'called only but got ' + objects.length + ' results';
                        }
                        var object = objects[0];
                        return cb ? cb(object) : object;
                    });
                }

                // repository

            }, {
                key: 'create',
                value: function create(object) {
                    options.body = JSON.stringify(object);
                    return this.wrappedRequest('POST');
                }
            }, {
                key: 'update',
                value: function update(object) {
                    options.url = object.id;
                    options.body = JSON.stringify(object);
                    return this.wrappedRequest('PUT');
                }
            }, {
                key: 'patch',
                value: function patch(object) {
                    options.url = object.id;
                    options.body = JSON.stringify(object);
                    return this.wrappedRequest('PATCH');
                }
            }, {
                key: 'destroy',
                value: function destroy(object) {
                    if (object) {
                        options.url = extractId(object);
                    }
                    return this.baseRequest('DELETE');
                }

                // actions

            }, {
                key: 'json',
                value: function json(object) {
                    options.body = JSON.stringify(object);
                    return this;
                }
            }, {
                key: 'params',
                value: function params(_params) {
                    options.query = (0, _utils.extend)(options.query, _params);
                    return this;
                }
            }, {
                key: 'param',
                value: function param(key, value) {
                    if (!options.query) {
                        options.query = {};
                    }
                    options.query[key] = value;
                }
            }, {
                key: 'action',
                value: function action(verb, path) {
                    options.url += '/' + path;
                    return this.baseRequest(verb);
                }
            }, {
                key: 'get',
                value: function get(action) {
                    return this.action('GET', action);
                }
            }, {
                key: 'put',
                value: function put(action) {
                    return this.action('PUT', action);
                }
            }, {
                key: '_patch',
                value: function _patch(action) {
                    return this.action('PATCH', action);
                }
            }, {
                key: 'post',
                value: function post(action) {
                    return this.action('POST', action);
                }
            }, {
                key: '_delete',
                value: function _delete(action) {
                    return this.action('DELETE', action);
                }

                // es5 subclassing

            }, {
                key: 'subclass',
                value: function subclass(constructorFn) {
                    var base = yawpFn(baseArg);
                    var _super = this;
                    var sub = function (_base) {
                        _inherits(sub, _base);

                        function sub() {
                            _classCallCheck(this, sub);

                            var _this4 = _possibleConstructorReturn(this, (sub.__proto__ || Object.getPrototypeOf(sub)).call(this));

                            _super.bindBaseMethods(_this4, base);
                            if (constructorFn) {
                                constructorFn.apply(_this4, arguments);
                            } else {
                                _get(sub.prototype.__proto__ || Object.getPrototypeOf(sub.prototype), 'constructor', _this4).apply(_this4, arguments);
                            }
                            return _this4;
                        }

                        return sub;
                    }(base);
                    sub.super = base;
                    return sub;
                }
            }, {
                key: 'bindBaseMethods',
                value: function bindBaseMethods(self, base) {
                    self.super = function () {};
                    var keys = Object.getOwnPropertyNames(base.prototype);
                    for (var i = 0, l = keys.length; i < l; i++) {
                        var key = keys[i];
                        self.super[key] = base.prototype[key].bind(self);
                    }
                }
            }]);

            return Yawp;
        }();

        Yawp.clear();
        return Yawp;
    }

    // base api

    function customFetchOptions(fn) {
        customFetchOptionsFn = fn;
    }

    function config(cb) {
        var c = {
            baseUrl: function baseUrl(url) {
                _baseUrl = url;
            },
            defaultFetchOptions: function defaultFetchOptions(options) {
                _defaultFetchOptions = options;
            },
            then: function then(fn) {
                _then = fn;
            },
            catch: function _catch(fn) {
                _catch2 = fn;
            }
        };
        cb(c);
    }

    function update(object) {
        var id = extractId(object);
        return yawpFn(id).update(object);
    }

    function patch(object) {
        var id = extractId(object);
        return yawpFn(id).patch(object);
    }

    function destroy(object) {
        var id = extractId(object);
        return yawpFn(id).destroy(object);
    }

    var baseApi = {
        config: config,
        customFetchOptions: customFetchOptions,
        update: update,
        patch: patch,
        destroy: destroy
    };

    return (0, _utils.extend)(yawpFn, baseApi);
};

module.exports = exports['default'];
//# sourceMappingURL=yawp.js.map