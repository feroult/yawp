package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class EndpointScaffolder extends Scaffolder {

    private static final String MODEL_TEMPLATE = "scaffolding/Endpoint.java.vm";

    private static final String MODEL_TEST_TEMPLATE = "scaffolding/EndpointTest.java.vm";

    private ShieldScaffolder shieldScaffolder;

    public EndpointScaffolder(Log log, String yawpPackage, String model) {
        super(log, yawpPackage, model);

        this.shieldScaffolder = new ShieldScaffolder(log, yawpPackage, model);
    }

    @Override
    public void execute(String baseDir) {
        sourceMainJava(baseDir, endpointNaming.getFilename(), MODEL_TEMPLATE);
        sourceTestJava(baseDir, endpointNaming.getTestFilename(), MODEL_TEST_TEMPLATE);

        shieldScaffolder.execute(baseDir);
    }

}
