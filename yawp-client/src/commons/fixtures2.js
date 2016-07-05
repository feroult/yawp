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
            this.promise = null;
        }

        config(callback) {
            callback(this);
        }

        reset() {
            return request(this.resetUrl, {
                method: 'GET'
            }).then(() => {
                // do not pass the result to the upstream
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
            return () => request(this.url(), {
                method: 'POST',
                json: true,
                body: JSON.stringify(data)
            }).then((object) => {
                this.api[key] = object;
                return object;
            });
        }

        createLazyPropertyLoader(key) {
            this.api[key] = this.fx.lazyProperties.reduce(function (map, name) {
                map[name] = {};
                return map;
            }, {});
        }
    }

    return new Fixtures();
}
