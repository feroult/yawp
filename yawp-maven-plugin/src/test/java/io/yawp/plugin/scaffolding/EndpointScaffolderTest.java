package io.yawp.plugin.scaffolding;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class EndpointScaffolderTest {

	@Test
	public void testTemplate() {
		EndpointScaffolder scaffolder = new EndpointScaffolder("yawpapp", "person");

		String clazzText = scaffolder.getClazzText();
		assertTrue(clazzText.indexOf("@Endpoint(path = \"/people\")") != -1);

		scaffolder.createTo("./target/scaffolding-test");
		assertTrue(new File("./target/scaffolding-test/models/person/Person.java").exists());
	}

}
