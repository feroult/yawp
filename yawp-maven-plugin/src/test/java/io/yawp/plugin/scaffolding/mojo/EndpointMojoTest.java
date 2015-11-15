package io.yawp.plugin.scaffolding.mojo;


public class EndpointMojoTest extends ScaffolderMojoTestCase {

	public void testCreateEndpoint() throws Exception {
		executeGoal("endpoint");

		assertSourceMain("/person/Person.java", "@Endpoint(path = \"/people\")");
		assertSourceTest("/person/PersonTest.java", "public class PersonTest extends EndpointTestCase");
	}

}
