package io.yawp.plugin.scaffolding.mojo;

public class ActionMojoTest extends ScaffolderMojoTestCase {

	public void testCreateAction() throws Exception {
		lookupMojo("action");
		setParameter("name", "activate");
		executeGoal();

		assertSourceMain("/person/PersonActivateAction.java", "xpto");
	}

}
