package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.c;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ShieldConditionsTest extends EndpointTestCase {

	@Test
	public void testEvaluateIncomingSameAsExisting() {
		BasicObject object1 = saveObject("xpto");
		BasicObject object2 = saveObject("xpto");
		BasicObject object3 = saveObject("xyz");

		assertTrue(createShieldConditions(Arrays.asList(object1, object2), c("stringValue", "=", "xpto")).evaluate());
		assertFalse(createShieldConditions(Arrays.asList(object1, object3), c("stringValue", "=", "xpto")).evaluate());
	}

	@Test
	public void testEvaluateIncomingDifferentExisting() {
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

		ShieldConditions conditions = new ShieldConditions(null, Arrays.asList(object));
		conditions.where(c("stringValue", "=", "xpto"));
		conditions.where(c("intValue", "=", 1));

		object.setStringValue("xpto");
		object.setIntValue(1);
		assertTrue(conditions.evaluate());

		object.setStringValue("xyz");
		object.setIntValue(1);
		assertFalse(conditions.evaluate());

		object.setStringValue("xpto");
		object.setIntValue(2);
		assertFalse(conditions.evaluate());

		object.setStringValue("xpto");
		object.setIntValue(1);
		assertTrue(conditions.evaluate());
	}

	@Test
	public void testParentTrueConditions() {
		Child child = saveChild("child", saveParent(1l, "parent"));

		ShieldConditions conditions = new ShieldConditions(null, Arrays.asList(child));
		conditions.where(c("name", "=", "child"));
		conditions.parentWhere(c("name", "=", "parent"));

		assertTrue(conditions.evaluate());
	}

	@Test
	public void testParentFalseConditions() {
		Child child = saveChild("child", saveParent(1l, "another-parent"));

		ShieldConditions conditions = new ShieldConditions(null, Arrays.asList(child));
		conditions.where(c("name", "=", "child"));
		conditions.parentWhere(c("name", "=", "parent"));

		assertFalse(conditions.evaluate());
	}

	private ShieldConditions createShieldConditions(IdRef<?> id, Object object, BaseCondition c) {
		return createShieldConditions(id, Arrays.asList(object), c);
	}

	private ShieldConditions createShieldConditions(List<?> objects, BaseCondition c) {
		return createShieldConditions(null, objects, c);
	}

	private ShieldConditions createShieldConditions(IdRef<?> id, List<?> objects, BaseCondition c) {
		ShieldConditions conditions = new ShieldConditions(id, objects);
		conditions.where(c);
		return conditions;
	}

	private BasicObject saveObject(String name) {
		BasicObject object = new BasicObject();
		object.setStringValue(name);
		yawp.save(object);
		return object;
	}

	protected Parent saveParent(Long id, String name) {
		Parent parent = new Parent();
		parent.setId(IdRef.create(yawp, Parent.class, id));
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
}
