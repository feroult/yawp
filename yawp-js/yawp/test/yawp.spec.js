import chai from 'chai';
import yawp from '../index';
import fx from '../fx';

chai.expect();

const expect = chai.expect;

yawp.config((c) => {
    c.baseUrl('http://localhost:8081/api');
});

fx.config((c) => {
    c.baseUrl('http://localhost:8081/fixtures');
    c.resetUrl('http://localhost:8081/_ah/yawp/datastore/delete-all');
    c.bind('parent', '/parents');
    c.bind('child', '/children');
    c.bind('job', '/jobs');
});

describe('YAWP! JS', () => {

    beforeEach((done) => {
        yawp.config(c => {
            c.then(undefined);
            c.catch(undefined);
            c.defaultFetchOptions({});
        });
        fx.reset().then(done);
    });

    describe('Client', () => {
        it('creates a parent', (done) => {
            yawp('/parents').create({name: 'xpto'}).then((retrievedParent) => {
                expect(retrievedParent.name).to.be.equal('xpto');
                done();
            });
        });

        it('fetches a parent', (done) => {
            let parent = {
                id: '/parents/2',
                name: 'xpto'
            };

            fx.parent('parent', parent);

            fx.load(() => {
                yawp('/parents/2').fetch((retrievedParent) => {
                    expect(retrievedParent.name).to.be.equal('xpto');
                    done();
                });
            });
        });

        it('changes field from options within before and uses it', async () => {
            let i = 0;
            yawp.config(c => {
                c.before(opt => {
                    opt.headers = { 'Secret-Panda': `m${i++}` };
                });
            });

            const r1 = await (yawp('/shielded_objects').get('header'));
            expect(r1).to.be.equal('action:m0');

            const r2 = await (yawp('/shielded_objects').get('header'));
            expect(r2).to.be.equal('action:m1');

            const r3 = await (yawp('/shielded_objects').get('header'));
            expect(r3).to.be.equal('action:m2');
        });

        it('returns new custom fetch options with before and uses it', async () => {
            let i = 0;
            yawp.config(c => {
                c.before(opt => ({ ...opt, headers: { 'Secret-Panda': `m${i++}` }}));
            });

            const r1 = await (yawp('/shielded_objects').get('header'));
            expect(r1).to.be.equal('action:m0');

            const r2 = await (yawp('/shielded_objects').get('header'));
            expect(r2).to.be.equal('action:m1');

            const r3 = await (yawp('/shielded_objects').get('header'));
            expect(r3).to.be.equal('action:m2');
        });

        it('set custom options with before in async fashion', async () => {
            let i = 0;
            const iGenerator = () => new Promise(r => setTimeout(r(i++)));

            yawp.config(c => {
                c.before(async opt => ({ ...opt, headers: { 'Secret-Panda': `m${await (iGenerator())}` }}));
            });

            const r1 = await (yawp('/shielded_objects').get('header'));
            expect(r1).to.be.equal('action:m0');

            const r2 = await (yawp('/shielded_objects').get('header'));
            expect(r2).to.be.equal('action:m1');

            const r3 = await (yawp('/shielded_objects').get('header'));
            expect(r3).to.be.equal('action:m2');
        });

        it('fetches raw', async () => {
            const parent = {
                id: '/parents/2',
                name: 'xpto'
            };

            fx.parent('parent', parent);

            yawp.config(c => {
                c.defaultFetchOptions({ raw: true });
            });

            await fx.load();

            const data = await yawp('/parents/2').fetch();

            expect(data.status).to.be.equal(200);

            const txt = await data.text();
            expect(typeof txt).to.be.equal('string');
            expect(JSON.parse(txt).name).to.be.equal('xpto');

            yawp.config(c => {
                c.then(e => e.json());
            });

            const data2 = await yawp('/parents/2').fetch();
            expect(data2.name).to.be.equal('xpto');
        });

        it('it patches parent', async () => {
            const parent = await yawp('/parents').create({ name: 'xpto' });
            expect(parent.name).to.be.equal('xpto');
            const newParent = await yawp(parent.id).patch({ name: 'bla' });
            expect(newParent.name).to.be.equal('bla');
        });
    });

    describe('global hooks', () => {
        it('has global then hook', (done) => {
            yawp.config(c => {
                c.then((attrs) => {
                    attrs.xpto = 'hello';
                    return attrs;
                });
            });

            let parent = {
                id: '/parents/2',
                name: 'xpto'
            };

            fx.parent('parent', parent);

            fx.load(() => {
                yawp('/parents/2').fetch((retrievedParent) => {
                    expect(retrievedParent.xpto).to.be.equal('hello');
                    done();
                });
            });
        });

        it('has global catch hook', (done) => {
            yawp.config(c => {
                c.catch((err) => {
                    throw 'xpto';
                });
            });

            yawp('/shielded_objects/1').fetch().catch((err) => {
                expect(err).to.be.equal('xpto');
                done();
            });
        });

    });

    describe('ES6 class inheritance', () => {

        let ParentFn = yawp('/parents');

        class Parent extends yawp('/parents') {
            static myCreate(object) {
                return this.create(object);
            }

            mySave() {
                return this.save();
            }
        }

        it('creates a class function', () => {
            expect(typeof ParentFn === 'function').to.be.true;
            expect(ParentFn.where).to.not.be.undefined;
        });

        it('creates a instance with properties', () => {
            let parent = new ParentFn({name: 'xpto'});
            expect(parent.name).to.be.equals('xpto');
        });

        it('allows static method overriding', (done) => {
            Parent.myCreate({name: 'xpto'}).then((retrievedParent) => {
                expect(retrievedParent.name).to.be.equal('xpto');
                done();
            });
        });

        it('allows instance method overriding ', (done) => {
            new Parent({name: 'xpto'}).mySave().then((parent) => {
                expect(parent.id).to.be.not.undefined;
                expect(parent.name).to.be.equals('xpto');
                done();
            });
        });

        it('creates itself', (done) => {
            var parent = new Parent({name: 'xpto'});
            parent.save().then((retrievedParent) => {
                expect(parent.id).to.be.not.undefined;
                expect(retrievedParent.id).to.be.not.undefined;
                expect(retrievedParent.name).to.be.equals('xpto');
                done();
            });
        });

        it('updates itself', (done) => {
            let parent = {
                id: '/parents/2',
                name: 'xpto'
            };

            fx.parent('parent', parent);

            fx.load(() => {
                parent.name = 'xpto2';
                new Parent(parent).save((retrievedParent) => {
                    expect(retrievedParent.id).to.be.equals('/parents/2');
                    expect(retrievedParent.name).to.be.equals('xpto2');
                    done();
                });
            });
        });

        it('destroys itself', (done) => {
            let parent = {
                id: '/parents/2',
                name: 'xpto'
            };

            fx.parent('parent', parent);

            fx.load(() => {
                new Parent(parent).destroy((retrievedId) => {
                    expect(retrievedId).to.be.equals('/parents/2');
                    done();
                });
            });
        });

        it('calls actions over itself', (done) => {
            let parentJson = {
                id: '/parents/2',
                name: 'xpto'
            };

            fx.parent('parent', parentJson);

            fx.load(() => {
                var parent = new Parent(parentJson);
                parent.get('all-http-verbs').then((response) => {
                    expect(response).to.be.equals('ok');
                    parent.put('all-http-verbs').then((response) => {
                        expect(response).to.be.equals('ok');
                        parent._patch('all-http-verbs').then((response) => {
                            expect(response).to.be.equals('ok');
                            parent.post('all-http-verbs').then((response) => {
                                expect(response).to.be.equals('ok');
                                parent._delete('all-http-verbs').then((response) => {
                                    expect(response).to.be.equals('ok');
                                    done();
                                });
                            });
                        });
                    });
                });
            });
        });

        it('returns a class instance when fetching', (done) => {
            fx.parent('parent', {
                id: '/parents/1',
                name: 'xpto'
            });

            fx.load(() => {
                Parent.fetch(1).then((parent) => {
                    expect(parent.constructor.name).to.be.equals('Parent');
                    done();
                });
            });
        });

        it('returns a class instance even for the regular yawp function fetch', (done) => {
            fx.parent('parent', {
                id: '/parents/1',
                name: 'xpto'
            });

            fx.load(() => {
                yawp('/parents').fetch(1).then((parent) => {
                    expect(parent.constructor.name).to.be.equals('Yawp');
                    done();
                });
            });
        });

        it('feches and saves using class instances', (done) => {
            fx.parent('parent', {
                id: '/parents/1',
                name: 'xpto'
            });

            fx.load(() => {
                Parent.fetch(1).then((parent) => {
                    parent.name = 'xpto2';
                    parent.save((retrievedParent) => {
                        expect(retrievedParent.name).to.be.equals('xpto2');
                        done();
                    });
                });
            });
        });

        it('returns an array of class instances when listing', (done) => {
            fx.parent('parent1', {name: 'xpto1'});
            fx.parent('parent2', {name: 'xpto2'});

            fx.load(() => {
                Parent.list((parents) => {
                    expect(parents[0].constructor.name).to.be.equals('Parent');
                    expect(parents[1].constructor.name).to.be.equals('Parent');
                    done();
                });
            });
        });

        it('returns an instance when using with static helpers to create/put/patch', (done) => {
            Parent.create({name: 'xpto'}).then((parent) => {
                expect(parent.constructor.name).to.be.equals('Parent');
                Parent.update(parent).then((parent) => {
                    expect(parent.constructor.name).to.be.equals('Parent');
                    Parent.patch(parent).then((parent) => {
                        expect(parent.constructor.name).to.be.equals('Parent');
                        done();
                    });
                });
            });
        });
    });

    describe('ES5 class inheritance', () => {

        let Parent = yawp('/parents').subclass(function (props) {
            if (props && props.name === 'change it in constructor') {
                props.name = 'xpto';
            }
            this.super.constructor(props);
        });

        Parent.myCreate = function (object) {
            return this.super.create(object);
        };

        Parent.prototype.mySave = function () {
            return this.super.save();
        };

        Parent.prototype.myAction = function () {
            return this.super.post('all-http-verbs');
        };

        it('inherits static methods', () => {
            expect(Parent.where).to.be.not.undefined;
        });

        it('inherits instance methods', () => {
            let parent = new Parent();
            expect(parent.save).to.be.not.undefined;
        });

        it('overrides static methods', (done) => {
            Parent.myCreate({name: 'xpto'}).then((retrievedParent) => {
                expect(retrievedParent.name).to.be.equal('xpto');
                done();
            });
        });

        it('overrides instance methods', (done) => {
            var parent = new Parent({name: 'xpto'});
            parent.mySave().then((retrievedParent) => {
                expect(parent.id).to.be.not.undefined;
                expect(retrievedParent.id).to.be.not.undefined;
                expect(retrievedParent.name).to.be.equals('xpto');
                done();
            });
        });

        it('overrides instance methods with arguments', (done) => {
            let parentJson = {
                id: '/parents/2',
                name: 'xpto'
            };

            fx.parent('parent', parentJson);

            fx.load(() => {
                let parent = new Parent(parentJson);
                parent.myAction().then((response) => {
                    expect(response).to.be.equals('ok');
                    done();
                });
            });
        });

        it('overrides constructor', (done) => {
            var parent = new Parent({name: 'change it in constructor'});
            parent.mySave().then((retrievedParent) => {
                expect(retrievedParent.name).to.be.equals('xpto');
                done();
            });
        });
    });

});