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

    it('is creates a class function', () => {
        expect(typeof ParentFn === 'function').to.be.true;
        expect(ParentFn.where).to.not.be.undefined;
    });

    it('it creates a instance with properties', () => {
        let parent = new ParentFn({name: 'xpto'});
        expect(parent.name).to.be.equals('xpto');
    });

    it('it saves itself', (done) => {
        new ParentFn({name: 'xpto'}).save().then((parent) => {
            expect(parent.id).to.be.not.undefined;
            expect(parent.name).to.be.equals('xpto');
            done();
        });
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

    it.only('updates itself when it has id', (done) => {
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

    // test save (create or update) on instance
    // test destroy on instance
    // test actions over instance
    // test queries/fetches return instances
    // test clear previous options

});