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

describe('Some YAWP! Fixtures tests in nodejs', () => {

    before((done) => {
        fx.reset().then(done);
    });

    it('loads doesnt load lazy fixtures', (done) => {
        fx.lazy.parent('p1', {name: 'xpto1'});
        fx.lazy.parent('p2', {name: 'xpto2'});

        fx.load((fixtures) => {
            expect(fixtures.length).to.be.empty;
            done();
        });
    });

    it('loads fixtures if needed', (done) => {
        fx.lazy.parent('p1', {name: 'xpto1'});
        fx.lazy.parent('p2', {name: 'xpto2'});

        fx.parent('p1');

        fx.load((fixtures) => {
            expect(fixtures.parent.p1.name).to.be.equals('xpto1');
            done();
        });
    });

});