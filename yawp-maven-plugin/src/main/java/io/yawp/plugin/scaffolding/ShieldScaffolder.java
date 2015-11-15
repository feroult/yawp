package io.yawp.plugin.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class ShieldScaffolder extends Scaffolder {

	private static final String SHIELD_TEMPLATE = "scaffolding/Shield.java";

	public ShieldScaffolder(Log log, String yawpPackage, String name) {
		super(log, yawpPackage, name);
	}

	@Override
	public void execute(String baseDir) {
		sourceMainJava(baseDir, endpointNaming.getShieldFilename(), SHIELD_TEMPLATE);
	}



}
