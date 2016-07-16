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


describe('YAWP! Client', () => {
    describe('Basic features', () => {

        before((done) => {
            fx.reset().then(done);
        });

        it('creates a parent', (done) => {
            let parent = {
                name: 'xpto'
            };
            yawp('/parents').create(parent).then((retrievedParent) => {
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

    describe("Class features", () => {

        let Parent = yawp('/parents');

        it('is creates a class function', () => {
            expect(typeof Parent === 'function').to.be.true;
            expect(Parent.where).to.not.be.undefined;
        });

        it('it creates a instance with properties', () => {
            let parent = new Parent({name: 'xpto'});
            expect(parent.name).to.be.equals('xpto');
        });

        it('it saves itself', (done) => {
            new Parent({name: 'xpto'}).save().then((parent) => {
                expect(parent.id).to.be.not.undefined;
                done();
            });
        });

    });
});
