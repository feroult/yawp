package io.yawp.repository.shields;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;
import io.yawp.utils.ServletTestCase;

import org.junit.Test;

public class ShieldTest extends ServletTestCase {

	@Test
	public void testNothingIsAllowed() {
		createObject();

		assertGetWithStatus("/shielded_objects", 404);
		assertGetWithStatus("/shielded_objects/1", 404);
		assertPostWithStatus("/shielded_objects", "{stringValue: 'xpto'}", 404);
		assertPutWithStatus("/shielded_objects/1", "{id:'/shielded_objects/1', stringValue: 'xpto'}", 404);
		assertDeleteWithStatus("/shielded_objects/1", 404);
		assertPutWithStatus("/shielded_objects/1/something", 404);
	}

	@Test
	public void testEverythingIsAllowed() {
		login("jim", "rock.com");

		assertGetWithStatus("/shielded_objects", 200);
	}

	private void createObject() {
		ShieldedObject object = new ShieldedObject();
		object.setId(IdRef.create(yawp, ShieldedObject.class, 1l));
		yawp.save(object);
	}

}
