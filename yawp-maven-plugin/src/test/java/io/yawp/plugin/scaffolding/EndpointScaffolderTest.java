package io.yawp.plugin.scaffolding;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EndpointScaffolderTest {

	@Test
	public void testTemplate() {
		EndpointScaffolder scaffolder = new EndpointScaffolder("yawpapp", "person");

		String clazzText = scaffolder.getClazzText();
		assertTrue(clazzText.indexOf("@Endpoint(path = \"/people\")") != -1);
	}

}
