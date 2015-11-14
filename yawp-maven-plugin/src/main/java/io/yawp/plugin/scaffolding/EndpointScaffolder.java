package io.yawp.plugin.scaffolding;

public class EndpointScaffolder extends Scaffolder {

	private static final String MODEL_TEMPLATE = "scaffolding/Endpoint.java";

	private static final String MODEL_TEST_TEMPLATE = "scaffolding/EndpointTest.java";

	private String modelContent;

	private String modelTestContent;

	public EndpointScaffolder(String yawpPackage, String name) {
		super(yawpPackage, name);
	}

	@Override
	protected void parse() {
		this.modelContent = parse(MODEL_TEMPLATE);
		this.modelTestContent = parse(MODEL_TEST_TEMPLATE);
	}

	@Override
	public void createTo(String baseDir) {
		createFile(endpointNaming.getFilename(sourceMainJava(baseDir)), modelContent);
		createFile(endpointNaming.getTestFilename(sourceTestJava(baseDir)), modelTestContent);
	}

	protected String getModelContent() {
		return modelContent;
	}

	protected String getModelTestContent() {
		return modelTestContent;
	}

}
