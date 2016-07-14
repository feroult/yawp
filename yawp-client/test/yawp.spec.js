import chai from 'chai';
import yawp, { fx } from '../index';

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

describe('YAWP! Regular client', () => {

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

describe("YAWP! New client", () => {

    it('is creates a class function', () => {
        let Person = new yawp('/people');
        expect(typeof Person === 'function').to.be.true;
        expect(Person.where).to.not.be.undefined;
    });

});
