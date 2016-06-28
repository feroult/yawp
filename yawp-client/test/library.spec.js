import chai from 'chai';
import yawp from '../src/index';

chai.expect();

const expect = chai.expect;

yawp.config((c) => {
    c.baseUrl('http://localhost:8080/api');
})

describe('Some YAWP! tests in nodejs', () => {
    it('creates a parent', (done) => {
        var parent = {
            name: 'xpto'
        };
        yawp('/parents').create(parent).done(function (retrievedParent) {
            expect(retrievedParent.name).to.be.equal('xpto');
            done();
        });
    });
});