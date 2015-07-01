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
	}

	private void createObject() {
		ShieldedObject object = new ShieldedObject();
		object.setId(IdRef.create(yawp, ShieldedObject.class, 1l));
		yawp.save(object);
	}

}
