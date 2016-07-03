import chai from 'chai';
import yawp, { fx } from '../node';

chai.expect();

const expect = chai.expect;

yawp.config((c) => {
    c.baseUrl('http://localhost:8080/api');
});

fx.config((c) => {
    c.baseUrl('http://localhost:8080/fixtures');
    c.resetUrl('http://localhost:8080/_ah/yawp/datastore/delete_all');
    c.bind('parent', '/parents');
});

describe('Some YAWP! tests in nodejs', () => {

    before((done) => {
        fx.reset().then(done);
    });

    it('creates a parent', (done) => {
        var parent = {
            name: 'xpto'
        };
        yawp('/parents').create(parent).then((retrievedParent) => {
            expect(retrievedParent.name).to.be.equal('xpto');
            done();
        });
    });

    it('fetches a parent', (done) => {
        var parent = {
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