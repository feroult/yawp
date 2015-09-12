(function(t, yawp, fx) {

	t.moduledef('people test', {
		testStart : function() {
			fx.reset();
		}
	});

	t.asyncTest("query", function(assert) {
		expect(2);

		fx.person('amy');
		fx.person('janes');

		var order = [ {
			p : 'name'
		} ];

		yawp('/people').order(order).list(function(people) {
			assert.equal(people[0].name, 'Amy Winehouse');
			assert.equal(people[1].name, 'Janes Joplin');
			t.start();
		});
	});

})(QUnit, yawp, yawp.fixtures);