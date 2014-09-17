(function(t, yawp, fx) {

	t.moduledef('parent custom action', {
		testStart : function() {
			fx.reset();
		}
	});

	t.asyncTest("over object", function(assert) {
		expect(1);

		var parent = fx.parent('parent', {
			name : 'xpto'
		});

		yawp.idRef(parent.id).put('touched').done(function(retrievedParent) {
			assert.equal(retrievedParent.name, 'touched xpto');
			t.start();
		});
	});

//	t.asyncTest("over collection", function(assert) {
//		expect(1);
//
//		fx.parent('parent', {
//			name : 'xpto1'
//		});
//
//		fx.parent('parent', {
//			name : 'xpto2'
//		});
//
//		yawp(children).save();
//
//		yawp(children).parentId(parent.id).save()
//
//		yawp.save(parent.id + '/children', children).done(function(retrievedChidren) {
//		}
//
//		yawp()
//
//		yawp(parent).save();
//
//		yawp(parent).save(function(retrievedParent) {
//
//		}).fail(function(error) {
//			console.log('fail');
//		});
//
//		yawp('/people').get('me');
//
//		yawp('/children').parent(parent).get
//
//		yawp('/children').from(parent).save(child);
//
//		yawp(child).save().done();
//
//		yawp('/children').save()
//
//		yawp(parent).save(child);
//
//		yawp(parent).query()
//
//		yawp(parent.id).put('touched')
//
//		yawp.ref('/parents').get('me');
//
//		yawp.ref(parent, '/children').save(child).done();
//
//		yawp('/children').save(child).done
//
//		yawp('/parents', parent).save(xxx);
//
//		yawp(parent, '/children', child).save
//
//		yawp.save('/parents', parent)
//
//		yawp.ref(parent.id + '/children').put
//
//		yawp.put('/parents').action('touched').done();
//
//		yawp.put(parent.id + '/children').action('/touched')
//
//		yawp('/children').from(parent).save(newchild);
//
//		yawp('/children').from(parent).save(newchild);
//
//		yawp.save(child);
//
//		yawp('/children').from(parent),where
//
//		yawp.query('/children').from(parent).where...
//
//		yawp('/children').put('touched');
//
//		yawp(child).put('touched');
//
//		yawp('/people').list();
//
//
//		yawp('/people').list();
//
//		yawp(xpto).fetch()
//
//		yawp('/people').save(xpto);
//
//		yawp('/people').save(xpto);
//
//		yawp('/chindren').from(parent.id).save(child).done();
//
//		yawp()
//
//		yawp('/parents/:id/children', parent.id).save(child);
//
//
//		yawp(xpto.id).fetch();
//
//
//
//		yawp.save(child);
//
//		yawp(child).put('touched');
//		yawp('/grandchidren').from(child).put('touched').done();
//
//
//		yawp.ref('/parents').put('touched').done(function(retrievedParent) {
//			assert.equal(retrievedParent.name, 'touched xpto');
//			t.start();
//		});
//	});

})(QUnit, yawp, yawp.fixtures);