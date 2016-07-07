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
    c.resetUrl('http://localhost:8081/_ah/yawp/datastore/delete_all');
    c.bind('parent', '/parents');
    c.bind('child', '/children');
    c.bind('job', '/jobs');
});

describe('YAWP! Fixtures', () => {

    beforeEach((done) => {
        fx.reset(true).then(done);
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

    it('creates the same fixture only one time', (done) => {
        fx.parent('p1', {
            id: '/parents/1'
        });

        fx.parent('p1').then((parent) => {
            expect(parent.id).to.be.equals('/parents/1');
            done();
        });
    });

    it('creates a fixture with parent id', (done) => {
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

    it('loads previous fixtures', (done) => {
        fx.parent('p1', {name: 'xpto1'});
        fx.parent('p2', {name: 'xpto2'});

        fx.load(() => {
            expect(fx.parent.p1.name).to.be.equals('xpto1');
            expect(fx.parent.p2.name).to.be.equals('xpto2');
            done();
        });
    });

    it('loads a fixture as a promise', (done) => {
        fx.parent('p1', {name: 'xpto1'});

        fx.load().then(() => {
            expect(fx.parent.p1.name).to.be.equals('xpto1');
        }).then(() => {
            fx.load(() => fx.parent.p1.name).then((name) => {
                expect(name).to.be.equals('xpto1');
                done();
            });
        });
    });

    it('loads a lazy fixture', (done) => {
        fx.lazy.parent('p1', {name: 'xpto'});
        fx.parent('p1');

        fx.load(() => {
            expect(fx.parent.p1.name).to.be.equals('xpto');
            done();
        });
    });

    it('loads only required lazy fixtures', () => {
        fx.lazy.parent('p1', {name: 'xpto1'});
        fx.lazy.parent('p2', {name: 'xpto2'});
        fx.parent('p1');

        fx.load(() => {
            expect(fx.parent.p1.name).to.be.equals('xpto1');
            expect(fx.parent.p2).to.be.undefined;
            done();
        });
    });

    it('loads a lazy fixtures with a reference id', (done) => {
        fx.lazy.job('j1', {});
        fx.lazy.parent('p1', {jobId: fx.lazy.job.j1.id});

        fx.parent('p1');

        fx.load(() => {
            expect(fx.parent.p1.jobId).to.be.equals(fx.job.j1.id);
            done();
        });
    });

    it('loads an inner object lazy property', (done) => {
        fx.lazy.job('j1', {});
        fx.lazy.parent('p1', {job: {id: fx.lazy.job.j1.id}});

        fx.parent('p1');

        fx.load(() => {
            expect(fx.parent.p1.job.id).to.be.equals(fx.job.j1.id);
            done();
        });
    });

    // test fx.map
    // test fx.fixture('xx').id ?

});