package io.yawp.repository.shields;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;
import io.yawp.utils.ServletTestCase;

import org.junit.Before;
import org.junit.Test;

public class ShieldTest extends ServletTestCase {

	@Before
	public void before() {
		createObject();
	}

	@Test
	public void testNothingIsAllowed() {
		assertRestActionsStatus(404);
	}

	@Test
	public void testEverythingIsAllowed() {
		login("jim", "rock.com");
		assertRestActionsStatus(200);
	}

	@Test
	public void testSomethingIsAllowed() {
		login("jane", "rock.com");
		assertGetWithStatus("/shielded_objects", 404);
		assertGetWithStatus("/shielded_objects/1", 200);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", 404);
		assertPutWithStatus("/shielded_objects/1", "{id:'/shielded_objects/1', stringValue: 'xpto'}", 200);
		assertDeleteWithStatus("/shielded_objects/1", 404);
		assertPutWithStatus("/shielded_objects/1/something", 200);
	}

	private void assertRestActionsStatus(int status) {
		assertGetWithStatus("/shielded_objects", status);
		assertGetWithStatus("/shielded_objects/1", status);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", status);
		assertPutWithStatus("/shielded_objects/1", "{id:'/shielded_objects/1', stringValue: 'xpto'}", status);
		assertDeleteWithStatus("/shielded_objects/1", status);
		assertPutWithStatus("/shielded_objects/1/something", status);
	}

	private void createObject() {
		ShieldedObject object = new ShieldedObject();
		object.setId(IdRef.create(yawp, ShieldedObject.class, 1l));
		yawp.save(object);
	}

}
