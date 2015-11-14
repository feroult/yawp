package io.yawp.plugin.scaffolding;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EndpointTest {

	@Test
	public void testTemplate() {
		Endpoint endpoint = new Endpoint("person");

		String clazzText = endpoint.getClazzText();
		assertTrue(clazzText.indexOf("@Endpoint(path = \"/People\")") != -1);
	}

}
