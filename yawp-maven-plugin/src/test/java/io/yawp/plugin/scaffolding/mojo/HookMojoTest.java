package io.yawp.plugin.scaffolding.mojo;

public class HookMojoTest extends ScaffolderMojoTestCase {

	public void testCreateTransformer() throws Exception {
		lookupGoal("hook");
		setParameter("name", "validate");
		executeGoal();

		assertSourceMain("/person/PersonValidateHook.java", "public class PersonValidateHook extends Hook<Person>");
	}

}
