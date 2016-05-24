package io.yawp.plugin.mojos.scaffolding.mojo;

public class TransformerMojoTest extends ScaffolderMojoTestCase {

    public void testCreateTransformer() throws Exception {
        lookupGoal("transformer");
        setParameter("name", "upper_case");
        executeGoal();

        assertSourceMain("/person/PersonUpperCaseTransformer.java", "public class PersonUpperCaseTransformer extends Transformer<Person>");
    }

}
