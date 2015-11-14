package io.yawp.plugin.scaffolding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public abstract class Scaffolder {

	private static final String SOURCE_MAIN_JAVA = "src/main/java";

	private static final String SOURCE_TEST_JAVA = "src/test/java";

	private static final String MODELS_FOLDER = "models";

	protected EndpointNaming endpointNaming;

	protected String yawpPackage;

	public Scaffolder(String yawpPackage, String name) {
		this.yawpPackage = yawpPackage;
		this.endpointNaming = new EndpointNaming(name);
		parse();
	}

	protected abstract void parse();

	public abstract void createTo(String baseDir);

	protected String parse(String scaffoldingTemplate) {
		VelocityContext context = new VelocityContext();
		context.put("yawpPackage", yawpPackage);
		context.put("endpoint", endpointNaming);
		return parseTemplate(scaffoldingTemplate, context);
	}

	private String parseTemplate(String scaffoldingTemplate, VelocityContext context) {
		VelocityEngine engine = createVelocityEngine();
		Template template = engine.getTemplate(scaffoldingTemplate);

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private VelocityEngine createVelocityEngine() {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		return ve;
	}

	protected void createFile(String filename, String content) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(getFile(filename)));
			pw.print(content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private File getFile(String filename) {
		File file = new File(filename);
		file.getParentFile().mkdirs();
		return file;
	}

	protected String sourceMainJava(String appDir) {
		return String.format("%s/%s/%s/%s", appDir, SOURCE_MAIN_JAVA, yawpPackageDir(), MODELS_FOLDER);
	}

	protected String sourceTestJava(String appDir) {
		return String.format("%s/%s/%s/%s", appDir, SOURCE_TEST_JAVA, yawpPackageDir(), MODELS_FOLDER);
	}

	private String yawpPackageDir() {
		return yawpPackage.replaceAll("\\.", "/");
	}

}
