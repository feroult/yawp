import { extend } from './utils';

const DEFAULT_BASE_URL = '/fixtures';
const DEFAULT_RESET_URL = '/_ah/yawp/datastore/delete_all';
const DEFAULT_LAZY_PROPERTIES = ['id']; // needed till harmony proxies

export default (request) => {

    class Fixtures {
        constructor() {
            this._baseUrl = DEFAULT_BASE_URL;
            this._resetUrl = DEFAULT_RESET_URL;
            this._lazyProperties = DEFAULT_LAZY_PROPERTIES;
            this.promise = null;
            this.fixtures = [];
            this.lazy = {};
        }

        config(callback) {
            callback(this);
        }

        baseUrl(url) {
            this._baseUrl = url;
        }

        resetUrl(url) {
            this._resetUrl = url;
        }

        lazyProperties(properties) {
            this._lazyProperties = properties;
        }

        reset(all) {
            return request(this._resetUrl, {
                method: 'GET'
            }).then(() => {
                this.clear(all);
            });
        }

        clear(all) {
            this.promise = null;
            for (let i = 0, l = this.fixtures.length; i < l; i++) {
                var name = this.fixtures[i].name;
                var path = this.fixtures[i].path;
                this.bindFixture(name, path);
                all && this.bindLazy(name, path);
            }
        }

        bind(name, path) {
            this.fixtures.push({name, path});
            this.bindFixture(name, path);
            this.bindLazy(name);
        }

        bindFixture(name, path) {
            this[name] = new Fixture(this, name, path).api;
        }

        bindLazy(name) {
            this.lazy[name] = new Lazy(this, name).api;
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
                return new Promise(() => callback && callback());
            }
            return this.promise.then(() => callback && callback());
        }
    }

    class Fixture {
        constructor(fx, name, path) {
            this.fx = fx;
            this.name = name;
            this.path = path;
            this.api = this.createApi();
        }

        createApi() {
            var api = (key, data) => {
                return this.fx.chain(this.load(key, data));
            };
            api.self = this;
            return api;
        }

        url() {
            return this.fx._baseUrl + this.path;
        }

        load(key, data) {
            this.createStubs(key);
            return this.createLoadPromiseFn(key, data);
        }

        createLoadPromiseFn(key, data) {
            if (!data) {
                data = this.getLazyDataFor(key);
            }

            return () => {
                if (this.isLoaded(key)) {
                    return this.api[key];
                }

                return this.prepare(data).then((object) => {
                    return request(this.url(), {
                        method: 'POST',
                        json: true,
                        body: JSON.stringify(object)
                    }).then((response) => {
                        this.api[key] = response;
                        return response;

                    })
                });
            }
        }

        getLazyDataFor(key) {
            var lazy = this.fx.lazy[this.name].self;
            return lazy.getData(key);
        }

        prepare(data) {
            return new Promise((resolve) => {
                let object = {};
                extend(object, data);

                let lazyProperties = [];
                this.inspectLazyProperties(object, lazyProperties);
                this.resolveLazyProperties(object, lazyProperties, resolve);
            });
        }

        resolveLazyProperties(object, lazyProperties, resolve) {
            if (!lazyProperties.length) {
                resolve(object);
            } else {
                var promise = lazyProperties[0]();
                for (var i = 1, l = lazyProperties.length; i < l; i++) {
                    promise = promise.then(lazyProperties[i]);
                }

                promise.then(() => {
                    resolve(object);
                });
            }
        }

        inspectLazyProperties(object, lazyProperties) {
            for (let key in object) {
                if (!object.hasOwnProperty(key)) {
                    continue;
                }

                let value = object[key];
                if (value instanceof Function) {
                    lazyProperties.push(() => {
                        return value().then((actualValue) => {
                            object[key] = actualValue;
                        });
                    });
                    continue;
                }
                if (value instanceof Object) {
                    this.inspectLazyProperties(value, lazyProperties);
                    return;
                }
            }
        }

        createStubs(key) {
            if (this.hasStubs(key)) {
                return;
            }
            let self = this;
            this.api[key] = this.fx._lazyProperties.reduce((map, property) => {
                map[property] = () => {
                    return new Promise((resolve) => resolve(self.api[key][property]));
                }
                return map;
            }, {});
            this.api[key].__stub__ = true;
        }

        isLoaded(key) {
            return this.api[key] && !this.hasStubs(key);
        }

        hasStubs(key) {
            return this.api[key] && this.api[key].__stub__;
        }
    }

    class Lazy {
        constructor(fx, name) {
            this.fx = fx;
            this.name = name;
            this.data = {};
            this.api = this.createApi();
        }

        createApi() {
            let api = (key, data) => {
                this.createLazyStubs(key);
                this.data[key] = data;
            };
            api.self = this;
            return api;
        }

        getData(key) {
            return this.data[key];
        }

        createLazyStubs(key) {
            if (this.hasStubs(key)) {
                return;
            }
            this.api[key] = this.fx._lazyProperties.reduce((map, property) => {
                map[property] = () => {
                    return this.getFixtureRef().load(key)().then((object) => object[property]);
                };
                return map;
            }, {});
            this.api[key].__stub__ = true;
        }

        hasStubs(key) {
            return this.api[key] && this.api[key].__stub__;
        }

        getFixtureRef() {
            return this.fx[this.name].self;
        }
    }

    return new Fixtures();
}
