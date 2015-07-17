package io.yawp.repository.shields;

import static org.junit.Assert.assertEquals;
import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ShieldTest extends ServletTestCase {

	@Test
	public void testNothingIsAllowed() {
		saveObject(1l);

		assertRestActionsStatus(404);
	}

	@Test
	public void testEverythingIsAllowed() {
		saveObject(1l);
		login("jim", "rock.com");

		assertRestActionsStatus(200);
	}

	@Test
	public void testSomethingIsAllowed() {
		saveObject(1l);
		login("jane", "rock.com");

		assertGetWithStatus("/shielded_objects", 404);
		assertGetWithStatus("/shielded_objects/1", 200);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", 404);
		assertPutWithStatus("/shielded_objects/1", "{id:'/shielded_objects/1', stringValue: 'xpto'}", 200);
		assertDeleteWithStatus("/shielded_objects/1", 404);
		assertPutWithStatus("/shielded_objects/1/something", 200);
	}

	@Test
	public void testRoutesWithIds() {
		saveObject(100l);

		assertGetWithStatus("/shielded_objects/100", 200);
		assertPutWithStatus("/shielded_objects/100", "{id:'/shielded_objects/100', stringValue: 'xpto'}", 200);
		assertDeleteWithStatus("/shielded_objects/100", 200);
	}

	@Test
	public void testRouteWithObject() {
		saveObject(200l);

		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", 404);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'valid object'}", 200);

		assertPostWithStatus("/shielded_objects", "[{stringValue: 'valid object'}, {stringValue: 'xpto'}]", 404);
		assertPostWithStatus("/shielded_objects", "[{stringValue: 'valid object'}, {stringValue: 'valid object'}]", 200);

		assertPutWithStatus("/shielded_objects/200", "{id:'/shielded_objects/200', stringValue: 'xpto'}", 404);
		assertPutWithStatus("/shielded_objects/200", "{id:'/shielded_objects/200', stringValue: 'valid object'}", 200);
	}

	@Test
	public void testDifferentActions() {
		assertPutWithStatus("/shielded_objects/1/something", 404);
		assertPutWithStatus("/shielded_objects/1/anotherthing", 404);
		assertPutWithStatus("/shielded_objects/100/anotherthing", 200);
		assertGetWithStatus("/shielded_objects/collection", 200);
	}

	@Test
	public void testActionWithParams() {
		Map<String, String> params = new HashMap<String, String>();

		params.put("x", "xpto");
		assertPutWithStatus("/shielded_objects/1/anotherthing", params, 404);

		params.put("x", "ok");
		assertPutWithStatus("/shielded_objects/1/anotherthing", params, 200);
	}

	@Test
	public void testIndexWhere() {
		saveObject(1l, "ok");
		saveObject(2l, "xpto");
		saveObject(3l, "xpto");

		login("kurt", "rock.com");

		String json = get("/shielded_objects");
		List<ShieldedObject> objects = fromList(json, ShieldedObject.class);

		assertEquals(1, objects.size());
		assertEquals("ok", objects.get(0).getStringValue());
	}

	@Test
	public void testShowWhere() {
		saveObject(1l, "ok");
		saveObject(2l, "xpto");

		login("kurt", "rock.com");

		assertGetWithStatus("/shielded_objects/1", 200);
		assertGetWithStatus("/shielded_objects/2", 404);
	}

	@Test
	public void testCreateWhere() {
		login("kurt", "rock.com");

		assertPostWithStatus("/shielded_objects", "{stringValue: 'ok'}", 200);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", 403);

		assertPostWithStatus("/shielded_objects", "[{stringValue: 'ok'}, {stringValue: 'ok'}]", 200);
		assertPostWithStatus("/shielded_objects", "[{stringValue: 'ok'}, {stringValue: 'xpto'}]", 403);
	}

	@Test
	public void testCreateWhereOnExistingObjects() {
		saveObject(1l, "ok-for-janis");
		saveObject(2l, "ok-for-janis");
		saveObject(3l, "ok-for-amy");

		login("janis", "rock.com");

		assertPostWithStatus("/shielded_objects", "{id: '/shielded_objects/1', stringValue: 'ok-for-janis'}", 200);
		assertPostWithStatus("/shielded_objects", "{id: '/shielded_objects/3', stringValue: 'ok-for-janis'}", 403);

		assertPostWithStatus("/shielded_objects",
				"[{id: '/shielded_objects/1', stringValue: 'ok-for-janis'}, {id: '/shielded_objects/2', stringValue: 'ok-for-janis'}]", 200);
		assertPostWithStatus("/shielded_objects",
				"[{id: '/shielded_objects/2', stringValue: 'ok-for-janis'}, {id: '/shielded_objects/3', stringValue: 'ok-for-janis'}]", 403);
	}

	@Test
	public void testUpdateWhere() {
		login("kurt", "rock.com");

		saveObject(1l, "ok");

		assertPutWithStatus("/shielded_objects/1", "{stringValue: 'ok'}", 200);
		assertPutWithStatus("/shielded_objects/1", "{stringValue: 'xpto'}", 403);
	}

	@Test
	public void testUpdateWhereOnExistingObjects() {
		saveObject(1l, "ok-for-janis");
		saveObject(2l, "ok-for-amy");

		login("janis", "rock.com");

		assertPutWithStatus("/shielded_objects/1", "{stringValue: 'ok-for-janis'}", 200);
		assertPutWithStatus("/shielded_objects/2", "{stringValue: 'ok-for-janis'}", 403);
	}

	@Test
	public void testDeleteOnExistingObjects() {
		saveObject(1l, "ok-for-janis");
		saveObject(2l, "ok-for-amy");

		login("janis", "rock.com");

		assertDeleteWithStatus("/shielded_objects/1", 200);
		assertDeleteWithStatus("/shielded_objects/2", 403);
	}

	@Test
	public void testActionOnExistingObject() {
		saveObject(1l, "ok-for-janis");
		saveObject(2l, "ok-for-amy");

		login("janis", "rock.com");

		assertPutWithStatus("/shielded_objects/1/something", 200);
		assertPutWithStatus("/shielded_objects/2/something", 403);
	}

	private void assertRestActionsStatus(int status) {
		assertGetWithStatus("/shielded_objects", status);
		assertGetWithStatus("/shielded_objects/1", status);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", status);
		assertPutWithStatus("/shielded_objects/1", "{id:'/shielded_objects/1', stringValue: 'xpto'}", status);
		assertDeleteWithStatus("/shielded_objects/1", status);
		assertPutWithStatus("/shielded_objects/1/something", status);
	}

	private void saveObject(long id) {
		saveObject(id, null);
	}

	private void saveObject(long id, String stringValue) {
		ShieldedObject object = new ShieldedObject();
		object.setId(IdRef.create(yawp, ShieldedObject.class, id));
		object.setStringValue(stringValue);
		yawp.save(object);
	}

}
