package io.yawp.repository.shields;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;
import io.yawp.utils.ServletTestCase;

import org.junit.Before;
import org.junit.Test;

public class ShieldTest extends ServletTestCase {

	@Before
	public void before() {
		createObject(1l);
	}

	@Test
	public void testNothingIsAllowed() {
		createObject(1l);

		assertRestActionsStatus(404);
	}

	@Test
	public void testEverythingIsAllowed() {
		createObject(1l);
		login("jim", "rock.com");

		assertRestActionsStatus(200);
	}

	@Test
	public void testSomethingIsAllowed() {
		createObject(1l);
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
		createObject(100l);

		assertGetWithStatus("/shielded_objects/100", 200);
		assertPutWithStatus("/shielded_objects/100", "{id:'/shielded_objects/100', stringValue: 'xpto'}", 200);
		assertDeleteWithStatus("/shielded_objects/100", 200);
	}

	@Test
	public void testRouteWithObject() {
		assertPostWithStatus("/shielded_objects", "{stringValue: 'invalid route with object'}", 404);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'valid route with object'}", 200);
		assertPostWithStatus("/shielded_objects", "[{stringValue: 'valid route with object'}, {stringValue: 'valid route with object'}]", 200);
		//assertPostWithStatus("/shielded_objects", "[{stringValue: 'valid route with object'}, {stringValue: 'xpto1'}]", 404);
	}

	private void assertRestActionsStatus(int status) {
		assertGetWithStatus("/shielded_objects", status);
		assertGetWithStatus("/shielded_objects/1", status);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", status);
		assertPutWithStatus("/shielded_objects/1", "{id:'/shielded_objects/1', stringValue: 'xpto'}", status);
		assertDeleteWithStatus("/shielded_objects/1", status);
		assertPutWithStatus("/shielded_objects/1/something", status);
	}

	private void createObject(long id) {
		createObject(id, null);
	}

	private void createObject(long id, String stringValue) {
		ShieldedObject object = new ShieldedObject();
		object.setId(IdRef.create(yawp, ShieldedObject.class, id));
		object.setStringValue(stringValue);
		yawp.save(object);
	}

}
