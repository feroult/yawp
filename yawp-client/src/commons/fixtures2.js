import { extend } from './utils';

const DEFAULT_BASE_URL = '/fixtures';
const DEFAULT_RESET_URL = '/_ah/yawp/datastore/delete_all';
const DEFAULT_LAZY_PROPERTY_KEYS = ['id']; // needed till harmony proxies

export default (request) => {

    class Fixtures {
        constructor() {
            this.baseUrl = DEFAULT_BASE_URL;
            this.resetUrl = DEFAULT_RESET_URL;
            this.lazyPropertyKeys = DEFAULT_LAZY_PROPERTY_KEYS;
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
                return () => this.load(key, data);
            };
        }

        load(key, data) {
            var url = this.fx.baseUrl + this.path;
            return request(url, {
                method: 'POST',
                json: true,
                body: JSON.stringify(data)
            }).then((object) => {
                this.api[key] = object;
                return object;
            });
        }
    }

    return new Fixtures();
}
