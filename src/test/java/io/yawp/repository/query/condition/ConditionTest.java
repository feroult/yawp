package io.yawp.repository.query.condition;

import static io.yawp.repository.query.condition.Condition.c;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Parent;

import java.util.Arrays;

import org.junit.Test;

public class ConditionTest extends EndpointTestCase {

	@Test
	public void testSimpleConditions() {
		BasicObject object = new BasicObject();
		object.setIntValue(10);

		assertTrue(c("intValue", "=", 10).evaluate(object));
		assertTrue(c("intValue", ">", 9).evaluate(object));
		assertTrue(c("intValue", ">=", 9).evaluate(object));
		assertTrue(c("intValue", ">=", 10).evaluate(object));
		assertTrue(c("intValue", "<", 11).evaluate(object));
		assertTrue(c("intValue", "<=", 11).evaluate(object));
		assertTrue(c("intValue", "<=", 10).evaluate(object));
		assertTrue(c("intValue", "!=", 5).evaluate(object));
		assertTrue(c("intValue", "IN", Arrays.asList(9, 10, 11)).evaluate(object));
		assertFalse(c("intValue", "IN", Arrays.asList(9, 11)).evaluate(object));
	}

	@Test
	public void testJoinedConditions() {
		BasicObject object = new BasicObject();
		object.setIntValue(10);
		object.setStringValue("ccc");
		object.setLongValue(100l);

		assertTrue(c("intValue", "=", 10).and(c("stringValue", "=", "ccc")).evaluate(object));
		assertFalse(c("intValue", "=", 10).and(c("stringValue", "!=", "ccc")).evaluate(object));
		assertTrue(c("intValue", "=", 9).or(c("stringValue", "=", "ccc")).evaluate(object));
		assertFalse(c("intValue", "=", 10).not().evaluate(object));
		assertTrue(c("intValue", "=", 9).not().evaluate(object));
		assertTrue(c("intValue", "=", 9).or(c("stringValue", "=", "bbb").or(c("longValue", "=", 100l))).evaluate(object));
	}

	@Test
	public void testParentRef() {
		Parent parent = new Parent("parent");
		yawp.save(parent);

		Child child = new Child("child", parent.getId());
		yawp.save(child);

		Grandchild grandchild = new Grandchild("grandchild", child.getId());
		yawp.save(grandchild);

		BaseCondition c = c("parent->parent->name", "=", "parent");
		c.init(yawp, Grandchild.class);

		assertTrue(c.evaluate(grandchild));
		assertTrue(c.evaluate(child));
		assertTrue(c.evaluate(parent));
	}

	@Test
	public void testIgnoreConditionForChildFields() {
		Parent parent = new Parent("parent");
		yawp.save(parent);

		Child child = new Child("child", parent.getId());
		yawp.save(child);

		Grandchild grandchild = new Grandchild("grandchild", child.getId());
		grandchild.setAge(10);
		yawp.save(grandchild);

		BaseCondition c = c("age", "=", 11);
		c.init(yawp, Grandchild.class);

		assertFalse(c.evaluate(grandchild));
		assertTrue(c.evaluate(child));
		assertTrue(c.evaluate(parent));

	}
}
