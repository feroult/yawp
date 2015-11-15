package io.yawp.plugin.scaffolding;

public class EndpointScaffolder extends Scaffolder {

	private static final String MODEL_TEMPLATE = "scaffolding/Endpoint.java";

	private static final String MODEL_TEST_TEMPLATE = "scaffolding/EndpointTest.java";

	private ShieldScaffolder shieldScaffolder;

	public EndpointScaffolder(String yawpPackage, String name) {
		super(yawpPackage, name);

		this.shieldScaffolder = new ShieldScaffolder(yawpPackage, name);
	}

	@Override
	public void createTo(String baseDir) {
		sourceMainJava(baseDir, endpointNaming.getFilename(), MODEL_TEMPLATE);
		sourceTestJava(baseDir, endpointNaming.getTestFilename(), MODEL_TEST_TEMPLATE);

		shieldScaffolder.createTo(baseDir);
	}

}
