package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.c;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ShieldConditionsTest extends EndpointTestCase {

    @Test
    public void testEvaluateIncomingEqualsExisting() {
        BasicObject object1 = saveObject("xpto");
        BasicObject object2 = saveObject("xpto");
        BasicObject object3 = saveObject("xyz");

        assertTrue(createShieldConditions(Arrays.asList(object1, object2), c("stringValue", "=", "xpto")).evaluate());
        assertFalse(createShieldConditions(Arrays.asList(object1, object3), c("stringValue", "=", "xpto")).evaluate());
    }

    @Test
    public void testEvaluateIncomingNotEqualsExisting() {
        BasicObject object1 = saveObject("xpto");
        BasicObject object2 = saveObject("xpto");
        BasicObject object3 = saveObject("xyz");
        object1.setStringValue("xyz");
        object2.setStringValue("xyz");

        assertFalse(createShieldConditions(Arrays.asList(object1, object3), c("stringValue", "=", "xpto")).evaluate());
        assertFalse(createShieldConditions(Arrays.asList(object2), c("stringValue", "=", "xpto")).evaluate());
        assertTrue(createShieldConditions(Arrays.asList(object3), c("stringValue", "=", "xyz")).evaluate());
    }

    @Test
    public void testEvaluateWithIdOrObjects() {
        BasicObject object = saveObject("xpto");
        object.setStringValue("xyz");

        assertFalse(createShieldConditions(object.getId(), object, c("stringValue", "=", "xyz")).evaluate());
        assertFalse(createShieldConditions(object.getId(), object, c("stringValue", "=", "xpto")).evaluate());
        assertTrue(createShieldConditions(object.getId(), null, c("stringValue", "=", "xpto")).evaluate());
    }

    @Test
    public void testChainnedConditions() {
        BasicObject object = new BasicObject();

        ShieldConditions conditions = new ShieldConditions(yawp, BasicObject.class, null, Arrays.asList(object));
        conditions.where(c("stringValue", "=", "xpto"));
        conditions.where(c("intValue", "=", 1));

        object.setStringValue("xpto");
        object.setIntValue(1);
        assertTrue(conditions.evaluate());

        object.setStringValue("xyz");
        object.setIntValue(1);
        assertTrue(conditions.evaluate());

        object.setStringValue("xpto");
        object.setIntValue(2);
        assertTrue(conditions.evaluate());

        object.setStringValue("xpto");
        object.setIntValue(1);
        assertTrue(conditions.evaluate());
    }

    @Test
    public void testParentTrueCondition() {
        Child child = saveChild("child", saveParent("parent"));

        ShieldConditions conditions = new ShieldConditions(yawp, Child.class, null, Arrays.asList(child));
        conditions.where(c("name", "=", "child"));
        conditions.where(c("parent->name", "=", "parent"));

        assertTrue(conditions.evaluate());
    }

    @Test
    public void testParentFalseCondition() {
        Child child = saveChild("child", saveParent("another-parent"));

        ShieldConditions conditions = new ShieldConditions(yawp, Child.class, null, Arrays.asList(child));
        conditions.where(c("name", "=", "child"));
        conditions.and(c("parent->name", "=", "parent"));

        assertFalse(conditions.evaluate());
    }

    @Test
    public void testParentWithoutObjectsTrueCondition() {
        Parent parent = saveParent("parent");

        ShieldConditions conditions = new ShieldConditions(yawp, Child.class, parent.getId(), null);
        conditions.where(c("name", "=", "child"));
        conditions.where(c("parent->name", "=", "parent"));

        assertTrue(conditions.evaluate());
    }

    @Test
    public void testParentWithoutObjectsFalseCondition() {
        Parent parent = saveParent("another-parent");

        ShieldConditions conditions = new ShieldConditions(yawp, Child.class, parent.getId(), null);
        conditions.where(c("name", "=", "child"));
        conditions.and(c("parent->name", "=", "parent"));

        assertFalse(conditions.evaluate());
    }

    @Test
    public void testGrandparentTrueCondition() {
        Grandchild grandchild = saveGrandchild("granchild", saveChild("child", saveParent("parent")));

        ShieldConditions conditions = new ShieldConditions(yawp, Grandchild.class, null, Arrays.asList(grandchild));
        conditions.where(c("name", "=", "granchild"));
        conditions.where(c("parent->name", "=", "child"));
        conditions.where(c("parent->parent->name", "=", "parent"));

        assertTrue(conditions.evaluate());
    }

    @Test
    public void testGrandparentFalseCondition() {
        Grandchild grandchild = saveGrandchild("granchild", saveChild("child", saveParent("another-parent")));

        ShieldConditions conditions = new ShieldConditions(yawp, Grandchild.class, null, Arrays.asList(grandchild));
        conditions.where(c("name", "=", "granchild"));
        conditions.and(c("parent->name", "=", "child"));
        conditions.and(c("parent->parent->name", "=", "parent"));

        assertFalse(conditions.evaluate());
    }

    @Test
    public void testGrandparentWithoutObjectTrueCondition() {
        Parent parent = saveParent("parent");
        saveGrandchild("granchild", saveChild("child", parent));

        ShieldConditions conditions = new ShieldConditions(yawp, Grandchild.class, parent.getId(), null);
        conditions.where(c("name", "=", "granchild"));
        conditions.and(c("parent->name", "=", "child"));
        conditions.and(c("parent->parent->name", "=", "parent"));

        assertTrue(conditions.evaluate());
    }

    @Test
    public void testGrandparentWithoutObjectFalseCondition() {
        Parent parent = saveParent("another-parent");
        saveGrandchild("granchild", saveChild("child", parent));

        ShieldConditions conditions = new ShieldConditions(yawp, Grandchild.class, parent.getId(), null);
        conditions.where(c("name", "=", "granchild"));
        conditions.and(c("parent->name", "=", "child"));
        conditions.and(c("parent->parent->name", "=", "parent"));

        assertFalse(conditions.evaluate());
    }

    private ShieldConditions createShieldConditions(IdRef<?> id, Object object, BaseCondition c) {
        return createShieldConditions(id, Arrays.asList(object), c);
    }

    private ShieldConditions createShieldConditions(List<?> objects, BaseCondition c) {
        return createShieldConditions(null, objects, c);
    }

    private ShieldConditions createShieldConditions(IdRef<?> id, List<?> objects, BaseCondition c) {
        ShieldConditions conditions = new ShieldConditions(yawp, BasicObject.class, id, objects);
        conditions.where(c);
        return conditions;
    }

    private BasicObject saveObject(String name) {
        BasicObject object = new BasicObject();
        object.setStringValue(name);
        yawp.save(object);
        return object;
    }

    protected Parent saveParent(String name) {
        Parent parent = new Parent();
        parent.setName(name);
        yawp.save(parent);
        return parent;
    }

    protected Child saveChild(String name, Parent parent) {
        Child child = new Child(name);
        child.setParentId(parent.getId());
        yawp.save(child);
        return child;
    }

    protected Grandchild saveGrandchild(String name, Child child) {
        Grandchild grandchild = new Grandchild(name);
        grandchild.setChildId(child.getId());
        yawp.save(grandchild);
        return grandchild;
    }
}
