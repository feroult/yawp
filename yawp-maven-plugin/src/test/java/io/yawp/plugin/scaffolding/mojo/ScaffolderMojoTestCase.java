package io.yawp.plugin.scaffolding.mojo;

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

	protected void lookupGoal(String goal) throws Exception {
		File pom = getTestFile("src/test/resources/pom.xml");
		mojo = lookupMojo(goal, pom);
	}

	protected void executeGoal(String goal) throws Exception, MojoExecutionException {
		lookupGoal(goal);
		executeGoal();
	}

	protected void executeGoal() throws MojoExecutionException, MojoFailureException {
		mojo.execute();
	}

	protected void assertSourceTest(String filename, String content) {
		File file = getSourceTest(filename);
		assertTrue(file.exists());
		assertFileContains(file, content);
	}

	protected void assertSourceMain(String filename, String content) {
		File file = getSourceMain(filename);
		assertTrue(file.exists());
		assertFileContains(file, content);
	}

	private File getSourceMain(String filename) {
		return new File("./target/scaffolding-test/src/main/java/yawpapp/models" + filename);
	}

	private File getSourceTest(String filename) {
		return new File("./target/scaffolding-test/src/test/java/yawpapp/models" + filename);
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
}
