package io.yawp.plugin.mojos.scaffolding.mojo;


public class EndpointMojoTest extends ScaffolderMojoTestCase {

    public void testCreateEndpoint() throws Exception {
        executeGoalJava("endpoint");

        assertSourceMain("/person/Person.java", "@Endpoint(path = \"/people\")", "java");
        assertSourceTest("/person/PersonTest.java", "public class PersonTest extends EndpointTestCase", "java");
        assertSourceMain("/person/PersonShield.java", "public class PersonShield extends Shield<Person>", "java");
    }

    public void testCreateEndpointKotlin() throws Exception {
        executeGoalKotlin("endpoint");

        assertSourceMain("/person/Person.kt", "@Endpoint(path = \"/people\")", "kotlin");
        assertSourceTest("/person/PersonTest.kt", "class PersonTest : EndpointTestCase()", "kotlin");
        assertSourceMain("/person/PersonShield.kt", "class PersonShield : Shield<Person>()", "kotlin");
    }


}
