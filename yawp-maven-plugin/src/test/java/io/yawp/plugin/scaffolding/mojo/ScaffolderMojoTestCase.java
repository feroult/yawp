package io.yawp.plugin.scaffolding.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public abstract class ScaffolderMojoTestCase extends AbstractMojoTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		FileUtils.deleteDirectory(new File("./target/scaffolding-test"));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected void executeGoal(String goal) throws Exception, MojoExecutionException {
		File pom = getTestFile("src/test/resources/pom.xml");
		lookupMojo(goal, pom).execute();
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

}
