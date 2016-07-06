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
            this.fixtures = [];
            this.lazy = {};
        }

        config(callback) {
            callback(this);
        }

        reset() {
            return request(this.resetUrl, {
                method: 'GET'
            }).then(() => {
                this.clear();
            });
        }

        clear(all) {
            this.promise = null;
            for (let {name, path} of this.fixtures) {
                this.bindFixture(name, path);
                all && this.bindLazy(name, path);
            }
        }

        bind(name, path) {
            this.fixtures.push({name, path});
            this.bindFixture(name, path);
            this.bindLazy(name, path);
        }

        bindFixture(name, path) {
            this[name] = new EndpointFixture(this, name, path).api;
        }

        bindLazy(name, path) {
            this.lazy[name] = new LazyFixture(this, name, path).api;
        }

        chain(promiseFn) {
            if (!this.promise) {
                this.promise = promiseFn();
            } else {
                this.promise = this.promise.then(promiseFn)
            }
            return this.promise;
        }

        load(callback) {
            if (!this.promise) {
                callback();
                return;
            }
            this.promise.then(() => {
                callback();
            });
        }

        getLazyFor(name, key) {
            var lazy = this.lazy[name].self;
            return lazy.getData(key);
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
            this.createPropertyStubs(key);
            return this.createLoadPromiseFn(key, data);
        }

        createLoadPromiseFn(key, data) {
            if (!data) {
                data = this.fx.getLazyFor(this.name, key);
            }

            return () => {
                if (this.isLoaded(key)) {
                    return this.api[key];
                }

                return request(this.url(), {
                    method: 'POST',
                    json: true,
                    body: JSON.stringify(this.prepare(data))
                }).then((response) => {
                    this.api[key] = response;
                    return response;
                });
            }
        }

        prepare(data) {
            let object = {};
            extend(object, data);

            for (let key of Object.keys(object)) {
                let value = object[key];
                if (value instanceof Function) {
                    object[key] = value();
                }
            }
            return object;
        }

        createPropertyStubs(key) {
            if (this.api[key]) {
                return;
            }
            let self = this;
            this.api[key] = this.fx.lazyProperties.reduce((map, name) => {
                map[name] = () => {
                    return self.api[key][name];
                }
                return map;
            }, {});
            this.api[key].__stub__ = true;
        }

        isLoaded(key) {
            return this.api[key] && !this.api[key].__stub__;
        }
    }

    class LazyFixture {
        constructor(fx, name, path) {
            this.fx = fx;
            this.name = name;
            this.path = path;
            this.data = {};
            this.api = this.createApi();
        }

        createApi() {
            let api = (key, data) => {
                this.data[key] = data;
            };
            api.self = this;
            return api;
        }

        getData(key) {
            return this.data[key];
        }
    }

    return new Fixtures();
}
