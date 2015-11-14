package io.yawp.plugin.scaffolding;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.atteo.evo.inflector.English;

public class Endpoint {

	private static final String SCAFFOLDING_TEMPLATE = "scaffolding/Endpoint.java";

	private String packageName;

	private String path;

	private String clazzText;

	private String clazz;

	public Endpoint(String name) {
		this.packageName = formatPackageName(name);
		this.path = formatPath(name);
		this.clazz = formatClazz(name);
		parse();
	}

	private String formatPackageName(String name) {
		return name.toLowerCase();
	}

	private String formatPath(String name) {
		return English.plural(name);
	}

	private String formatClazz(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	private void parse() {
		VelocityContext context = new VelocityContext();
		context.put("endpoint", this);
		context.put("xpto", "hello!");

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

	public String getClazzText() {
		return clazzText;
	}
}
