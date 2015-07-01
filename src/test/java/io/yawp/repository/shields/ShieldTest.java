package io.yawp.repository.shields;

import io.yawp.utils.ServletTestCase;

import org.junit.Test;

public class ShieldTest extends ServletTestCase {

	@Test
	public void testNothingIsAllowed() {
		assertGetWithStatus("/shielded_objects", 404);
	}

}
