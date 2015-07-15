package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.c;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ShieldWhereTest extends EndpointTestCase {

	@Test
	public void testEvaluateIncoming() {
		BasicObject object1 = saveObject("xpto");
		BasicObject object2 = saveObject("xpto");
		BasicObject object3 = saveObject("xyz");

		assertTrue(createShieldWhere(Arrays.asList(object1, object2), c("stringValue", "=", "xpto")).evaluateIncoming());
		assertFalse(createShieldWhere(Arrays.asList(object1, object3), c("stringValue", "=", "xpto")).evaluateIncoming());
	}

	@Test
	public void testEvaluateExisting() {
		BasicObject object1 = saveObject("xpto");
		BasicObject object2 = saveObject("xpto");
		BasicObject object3 = saveObject("xyz");
		object1.setStringValue("xyz");
		object2.setStringValue("xyz");

		assertTrue(createShieldWhere(Arrays.asList(object1, object2), c("stringValue", "=", "xpto")).evaluateExisting());
		assertFalse(createShieldWhere(Arrays.asList(object1, object3), c("stringValue", "=", "xpto")).evaluateExisting());
	}

	@Test
	public void testEvaluateWithIdOrObjects() {
		BasicObject object = saveObject("xpto");
		object.setStringValue("xyz");

		assertTrue(createShieldWhere(object.getId(), object, c("stringValue", "=", "xyz")).evaluateIncoming());
		assertTrue(createShieldWhere(object.getId(), object, c("stringValue", "=", "xpto")).evaluateExisting());
		assertTrue(createShieldWhere(object.getId(), null, c("stringValue", "=", "xpto")).evaluateExisting());
	}

	@Test
	public void testChainnedConditions() {
		BasicObject object = new BasicObject();

		ShieldWhere where = new ShieldWhere(null, Arrays.asList(object));
		where.condition(c("stringValue", "=", "xpto"));
		where.condition(c("intValue", "=", 1));

		object.setStringValue("xpto");
		object.setIntValue(1);
		assertTrue(where.evaluateIncoming());

		object.setStringValue("xyz");
		object.setIntValue(1);
		assertFalse(where.evaluateIncoming());

		object.setStringValue("xpto");
		object.setIntValue(2);
		assertFalse(where.evaluateIncoming());

		object.setStringValue("xpto");
		object.setIntValue(1);
		assertTrue(where.evaluateIncoming());
	}

	private ShieldWhere createShieldWhere(IdRef<?> id, Object object, BaseCondition c) {
		return createShieldWhere(id, Arrays.asList(object), c);
	}

	private ShieldWhere createShieldWhere(List<?> objects, BaseCondition c) {
		return createShieldWhere(null, objects, c);
	}

	private ShieldWhere createShieldWhere(IdRef<?> id, List<?> objects, BaseCondition c) {
		ShieldWhere where = new ShieldWhere(id, objects);
		where.condition(c);
		return where;
	}

	private BasicObject saveObject(String name) {
		BasicObject object = new BasicObject();
		object.setStringValue(name);
		yawp.save(object);
		return object;
	}
}
