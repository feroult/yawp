package io.yawp.plugin.scaffolding;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class EndpointScaffolder {

	private static final String SCAFFOLDING_TEMPLATE = "scaffolding/Endpoint.java";

	private static final String SCAFFOLDING_FOLDER = "models";

	private String clazzText;

	private EndpointNaming endpointNaming;

	private String yawpPackage;

	public EndpointScaffolder(String yawpPackage, String name) {
		this.yawpPackage = yawpPackage;
		this.endpointNaming = new EndpointNaming(name);
		parse();
	}

	private void parse() {
		VelocityContext context = new VelocityContext();
		context.put("yawpPackage", yawpPackage);
		context.put("endpoint", endpointNaming);

		this.clazzText = parseTemplate(SCAFFOLDING_TEMPLATE, context);
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

	protected String getClazzText() {
		return clazzText;
	}

	public void createTo(String appDir) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(getFilename(appDir)));
			pw.print(clazzText);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private String getFilename(String appDir) {
		String dir = String.format("%s/%s/%s", appDir, yawpPackage, SCAFFOLDING_FOLDER).replaceAll("\\.", "/");
		return String.format("%s/%s", dir, endpointNaming.getFilename());
	}
}
