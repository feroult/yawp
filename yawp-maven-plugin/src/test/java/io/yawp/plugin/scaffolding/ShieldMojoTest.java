package io.yawp.plugin.scaffolding;

public class ShieldMojoTest extends ScaffolderMojoTestCase {

	public void testCreateShield() throws Exception {
		executeGoal("shield");

		assertSourceMain("/person/PersonShield.java", "public class PersonShield extends Shield<Person>");
	}

}
