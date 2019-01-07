package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class EndpointScaffolder extends Scaffolder {

    private static final String MODEL_TEMPLATE = "scaffolding/${lang}/Endpoint.${ext}.vm";

    private static final String MODEL_TEST_TEMPLATE = "scaffolding/${lang}/EndpointTest.${ext}.vm";

    private ShieldScaffolder shieldScaffolder;

    public EndpointScaffolder(Log log, String lang, String yawpPackage, String model) {
        super(log, lang, yawpPackage, model);

        this.shieldScaffolder = new ShieldScaffolder(log, lang, yawpPackage, model);
    }

    @Override
    public void execute(String baseDir) {
        sourceMain(baseDir, naming.getFilename(), naming.parsePath(MODEL_TEMPLATE));
        sourceTest(baseDir, naming.getTestFilename(), naming.parsePath(MODEL_TEST_TEMPLATE));

        shieldScaffolder.execute(baseDir);
    }

}
