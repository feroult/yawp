package io.yawp.repository.query;

import static io.yawp.repository.query.Condition.c;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.yawp.repository.models.basic.BasicObject;

import org.junit.Test;

public class ConditionTest {

	@Test
	public void testSimpleConditions() {
		BasicObject object = new BasicObject();
		object.setStringValue("xpto");

		assertTrue(c("stringValue", "=", "xpto").evaluate(object));
		assertFalse(c("stringValue", "!=", "xpto").evaluate(object));
	}
}
