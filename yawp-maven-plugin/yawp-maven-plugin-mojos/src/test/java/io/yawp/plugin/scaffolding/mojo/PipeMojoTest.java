package io.yawp.plugin.scaffolding.mojo;

public class PipeMojoTest extends ScaffolderMojoTestCase {

    public void testCreatePipe() throws Exception {
        lookupGoal("pipe");
        setParameter("name", "counter");
        setParameter("sink", "counter");
        executeGoal();

        assertSourceMain("/person/PersonCounterPipe.java", "public class PersonCounterPipe extends Pipe<Person, Counter>");
    }

}
