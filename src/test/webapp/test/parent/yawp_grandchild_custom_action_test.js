(function(t, yawp, fx) {

  t.module('grandchild custom action');

  t.testStart(function() {
    fx.reset();
  });

  t.asyncTest('action over object', function(assert) {
    // Grandchild grandchild = saveGrandchild("xpto", child);
    // String json = put(uri("/parents/%s/children/%s/grandchildren/%s/touched", parent, child, grandchild));
    // Grandchild retrievedGrandchild = from(json, Grandchild.class);
    // assertEquals("touched xpto", retrievedGrandchild.getName());
    // assertEquals(child.getId(), retrievedGrandchild.getChildId());
  });

  t.asyncTest('action over collection', function(assert) {
    // TODO
  });

})(QUnit, yawp, yawp.fixtures);
