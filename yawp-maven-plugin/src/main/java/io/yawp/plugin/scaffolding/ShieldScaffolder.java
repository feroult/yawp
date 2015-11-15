package io.yawp.plugin.scaffolding;

public class ShieldScaffolder extends Scaffolder {

	private static final String SHIELD_TEMPLATE = "scaffolding/Shield.java";

	public ShieldScaffolder(String yawpPackage, String name) {
		super(yawpPackage, name);
	}

	@Override
	public void createTo(String baseDir) {
		sourceMainJava(baseDir, endpointNaming.getShieldFilename(), SHIELD_TEMPLATE);
	}



}
