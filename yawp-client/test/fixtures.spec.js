import chai from 'chai'
import yawp, { fx } from '../node';
chai.expect();

const expect = chai.expect;

yawp.config((c) => {
    c.baseUrl('http://localhost:8080/api');
});

fx.config((c) => {
    c.baseUrl = 'http://localhost:8080/fixtures';
    c.resetUrl = 'http://localhost:8080/_ah/yawp/datastore/delete_all';
    c.bind('parent', '/parents');
    c.bind('child', '/children');
    c.bind('job', '/jobs');
});

describe('Some YAWP! Fixtures tests in nodejs', () => {

    beforeEach((done) => {
        fx.reset().then(done);
    });

    it('creates a fixture', (done) => {
        fx.parent('p1', {
            name: 'xpto'
        }).then((parent) => {
            expect(parent.id).to.not.be.undefined;
            expect(parent.name).to.be.equals('xpto');
            expect(fx.parent.p1.id).to.not.be.undefined;
            expect(fx.parent.p1.name).to.be.equals('xpto');
            done();
        });
    });

    it('creates a fixture with parentId', (done) => {
        fx.child('c1', {
            parentId: '/parents/1'
        }).then((child) => {
            expect(child.id).to.contain('/parents/1');
            expect(child.parentId).to.be.equals('/parents/1');
            done();
        });

    });

    it('creates a fixture with a reference id', (done) => {
        fx.job('j1', {
            id: '/jobs/10'
        });

        fx.parent('p1', {
            jobId: fx.job.j1.id
        }).then((parent) => {
            expect(parent.jobId).to.be.equals('/jobs/10');
            done();
        });
    });


    // test reference ids
    // test load
    // test lazy

    //it('loads doesnt load lazy fixtures', (done) => {
    //    fx.lazy.parent('p1', {name: 'xpto1'});
    //    fx.lazy.parent('p2', {name: 'xpto2'});
    //
    //    fx.load((fixtures) => {
    //        expect(fixtures.length).to.be.empty;
    //        done();
    //    });
    //});
    //
    //it('loads fixtures if needed', (done) => {
    //    fx.lazy.parent('p1', {name: 'xpto1'});
    //    fx.lazy.parent('p2', {name: 'xpto2'});
    //
    //    fx.parent('p1');
    //
    //    fx.load((fixtures) => {
    //        expect(fixtures.parent.p1.name).to.be.equals('xpto1');
    //        done();
    //    });
    //});

});