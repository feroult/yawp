package io.yawp.plugin.mojos.scaffolding.mojo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public abstract class ScaffolderMojoTestCase extends AbstractMojoTestCase {

    private Mojo mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FileUtils.deleteDirectory(new File("./target/scaffolding-test"));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void lookupGoal(String goal, String lang) throws Exception {
        File pom = getTestFile(parsePath("src/test/resources/pom-${lang}.xml", lang));
        mojo = lookupMojo(goal, pom);
    }

    protected void executeGoalJava(String goal) throws Exception, MojoExecutionException {
        lookupGoalJava(goal);
        executeGoal();
    }

    protected void executeGoalKotlin(String goal) throws Exception, MojoExecutionException {
        lookupGoalKotlin(goal);
        executeGoal();
    }

    protected void lookupGoalJava(String goal) throws Exception {
        lookupGoal(goal, "java");
    }

    private void lookupGoalKotlin(String goal) throws Exception {
        lookupGoal(goal, "kotlin");
    }

    protected void executeGoal() throws MojoExecutionException, MojoFailureException {
        mojo.execute();
    }

    protected void assertSourceTest(String filename, String content, String lang) {
        File file = getSourceTest(filename, lang);
        assertTrue(file.exists());
        assertFileContains(file, content);
    }

    protected void assertSourceMain(String filename, String content, String lang) {
        File file = getSourceMain(filename, lang);
        assertTrue(file.exists());
        assertFileContains(file, content);
    }

    private File getSourceMain(String filename, String lang) {
        return new File(parsePath("./target/scaffolding-test/src/main/${lang}/yawpapp/models" + filename, lang));
    }

    private File getSourceTest(String filename, String lang) {
        return new File(parsePath("./target/scaffolding-test/src/test/${lang}/yawpapp/models" + filename, lang));
    }

    private void assertFileContains(File file, String content) {
        try {
            String fileContent = new String(Files.readAllBytes(file.toPath()));
            assertTrue(fileContent.indexOf(content) != -1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setParameter(String parameter, String value) {
        try {
            Field field = getFieldRecursively(mojo.getClass(), parameter);
            field.setAccessible(true);
            field.set(mojo, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getFieldRecursively(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("Can find parameter: " + fieldName);
    }

    private String parsePath(String path, String lang) {
        return path.replaceAll("\\$\\{lang\\}", lang);
    }

}
