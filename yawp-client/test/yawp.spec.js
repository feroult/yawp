import chai from 'chai';
import { fx } from '../index';

// TODO: fix import
import request from '../src/node/request';
var yawp = require('../src/commons/yawp2')(request);

chai.expect();

const expect = chai.expect;

yawp.config((c) => {
    c.baseUrl('http://localhost:8081/api');
});

fx.config((c) => {
    c.baseUrl('http://localhost:8081/fixtures');
    c.resetUrl('http://localhost:8081/_ah/yawp/datastore/delete_all');
    c.bind('parent', '/parents');
    c.bind('child', '/children');
    c.bind('job', '/jobs');
});

describe('YAWP! Regular Client', () => {
    beforeEach((done) => {
        fx.reset().then(done);
    });

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
});

describe("YAWP! Class Client", () => {

    beforeEach((done) => {
        fx.reset().then(done);
    });

    let ParentFn = yawp('/parents');

    class Parent extends yawp('/parents') {

        static myCreate(object) {
            return super.create(object);
        }

        mySave() {
            return super.save();
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

    // test queries/fetches return instances
    // test es5 inheritance

});