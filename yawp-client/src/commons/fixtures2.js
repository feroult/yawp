import { extend } from './utils';

const DEFAULT_BASE_URL = '/fixtures';
const DEFAULT_RESET_URL = '/_ah/yawp/datastore/delete_all';
const DEFAULT_LAZY_PROPERTIES = ['id']; // needed till harmony proxies

export default (request) => {

    class Fixtures {
        constructor() {
            this.baseUrl = DEFAULT_BASE_URL;
            this.resetUrl = DEFAULT_RESET_URL;
            this.lazyProperties = DEFAULT_LAZY_PROPERTIES;
            this.init();
        }

        init() {
            this.promise = null;
        }

        config(callback) {
            callback(this);
        }

        reset() {
            return request(this.resetUrl, {
                method: 'GET'
            }).then(() => {
                this.init();
            });
        }

        bind(name, path) {
            this[name] = new EndpointFixture(this, name, path).api;
        }

        chain(promiseFn) {
            if (!this.promise) {
                this.promise = promiseFn();
                return this.promise;
            }
            return this.promise.then(promiseFn);
        }

    }

    class EndpointFixture {
        constructor(fx, name, path) {
            this.fx = fx;
            this.name = name;
            this.path = path;
            this.api = this.createApi();
        }

        createApi() {
            return (key, data) => {
                return this.fx.chain(this.load(key, data));
            };

        }

        url() {
            return this.fx.baseUrl + this.path;
        }

        load(key, data) {
            this.createLazyPropertyLoader(key);
            return this.createLoadPromiseFn(key, data);
        }

        createLoadPromiseFn(key, data) {
            return () =>
                request(this.url(), {
                    method: 'POST',
                    json: true,
                    body: JSON.stringify(this.prepare(data))
                }).then((response) => {
                    this.api[key] = response;
                    return response;
                });
        }

        prepare(data) {
            var object = {};
            extend(object, data);

            for (var key in object) {
                if (!object.hasOwnProperty(key)) {
                    continue;
                }
                var value = object[key];
                if (value instanceof Function) {
                    object[key] = value();
                }
            }
            return object;
        }

        createLazyPropertyLoader(key) {
            if (this.api[key]) {
                return;
            }
            var self = this;
            this.api[key] = this.fx.lazyProperties.reduce(function (map, name) {
                map[name] = () => {
                    //console.log('y', self.api[key]);
                    return self.api[key][name];
                }
                return map;
            }, {});
        }
    }

    return new Fixtures();
}
