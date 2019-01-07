package io.yawp.plugin.mojos.scaffolding.mojo;

public class HookMojoTest extends ScaffolderMojoTestCase {

    public void testCreateHook() throws Exception {
        lookupGoalJava("hook");
        setParameter("name", "validate");
        executeGoal();

        assertSourceMain("/person/PersonValidateHook.java", "public class PersonValidateHook extends Hook<Person>", "java");
    }

}
