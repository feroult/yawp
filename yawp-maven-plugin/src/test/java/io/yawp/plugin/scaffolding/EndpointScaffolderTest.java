package io.yawp.plugin.scaffolding;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EndpointScaffolderTest {

	@Test
	public void testTemplate() {
		EndpointScaffolder scaffolder = new EndpointScaffolder("yawpapp", "person");

		assertTrue(scaffolder.getModelContent().indexOf("@Endpoint(path = \"/people\")") != -1);
		assertTrue(scaffolder.getModelTestContent().indexOf("public class PersonTest extends EndpointTestCase") != -1);
	}

}
