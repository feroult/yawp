package io.yawp.plugin.scaffolding;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class EndpointMojoTest extends AbstractMojoTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		getGeneratedFile().delete();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateEndpoint() throws Exception {
		File pom = getTestFile("src/test/resources/pom.xml");

		EndpointMojo endpointMojo = (EndpointMojo) lookupMojo("endpoint", pom);
		assertNotNull(endpointMojo);
		endpointMojo.execute();

		assertTrue(getGeneratedFile().exists());
	}

	private File getGeneratedFile() {
		return new File("./target/scaffolding-test/src/main/java/yawpapp/models/person/Person.java");
	}

}
