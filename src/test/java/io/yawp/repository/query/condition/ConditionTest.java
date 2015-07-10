package io.yawp.repository.query.condition;

import static io.yawp.repository.query.condition.Condition.c;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import io.yawp.repository.models.basic.BasicObject;

import org.junit.Test;

public class ConditionTest {

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
}
