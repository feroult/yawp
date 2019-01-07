package io.yawp.plugin.mojos.scaffolding.mojo;

public class ActionMojoTest extends ScaffolderMojoTestCase {

    public void testCreateAction() throws Exception {
        lookupGoalJava("action");
        setParameter("name", "activate");
        executeGoal();

        assertSourceMain("/person/PersonActivateAction.java", "public class PersonActivateAction extends Action<Person>", "java");
    }

}
