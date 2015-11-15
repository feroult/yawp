package io.yawp.plugin.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class EndpointScaffolder extends Scaffolder {

	private static final String MODEL_TEMPLATE = "scaffolding/Endpoint.java";

	private static final String MODEL_TEST_TEMPLATE = "scaffolding/EndpointTest.java";

	private ShieldScaffolder shieldScaffolder;

	public EndpointScaffolder(Log log, String yawpPackage, String name) {
		super(log, yawpPackage, name);

		this.shieldScaffolder = new ShieldScaffolder(log, yawpPackage, name);
	}

	@Override
	public void execute(String baseDir) {
		sourceMainJava(baseDir, endpointNaming.getFilename(), MODEL_TEMPLATE);
		sourceTestJava(baseDir, endpointNaming.getTestFilename(), MODEL_TEST_TEMPLATE);

		shieldScaffolder.execute(baseDir);
	}

}
