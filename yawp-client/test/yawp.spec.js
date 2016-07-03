import chai from 'chai';
import yawp from '../node';

var fx = yawp.fixtures;

chai.expect();

const expect = chai.expect;

yawp.config((c) => {
    c.baseUrl('http://localhost:8080/api');
});

fx.config((c) => {
    c.baseUrl('http://localhost:8080/fixtures');
    c.bind('parent', '/parents');
});

describe('Some YAWP! tests in nodejs', () => {

    //before(() => {
    //    fx.reset();
    //});
    //
    ////it('creates a parent', (done) => {
    ////    var parent = {
    ////        name: 'xpto'
    ////    };
    ////    yawp('/parents').create(parent).done((retrievedParent) => {
    ////        expect(retrievedParent.name).to.be.equal('xpto');
    ////        done();
    ////    });
    ////});
    //
    //
    //it('fetches a parent', () => {
    //    var parent = {
    //        id: '/parents/2',
    //        name: 'xpto'
    //    };
    //
    //    fx.parent('parent', parent);
    //
    //    //yawp('/parents/2').fetch((retrievedParent) => {
    //    //    expect(retrievedParent.name).to.be.equal('xpto');
    //    //    done();
    //    //});
    //});
});