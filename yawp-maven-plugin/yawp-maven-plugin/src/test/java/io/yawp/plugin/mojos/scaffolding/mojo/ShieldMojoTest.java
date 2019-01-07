package io.yawp.plugin.mojos.scaffolding.mojo;

public class ShieldMojoTest extends ScaffolderMojoTestCase {

    public void testCreateShield() throws Exception {
        executeGoalJava("shield");

        assertSourceMain("/person/PersonShield.java", "public class PersonShield extends Shield<Person>", "java");
    }

}
