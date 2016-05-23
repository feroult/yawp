package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class ShieldScaffolder extends Scaffolder {

    private static final String SHIELD_TEMPLATE = "scaffolding/Shield.java.vm";

    public ShieldScaffolder(Log log, String yawpPackage, String model) {
        super(log, yawpPackage, model);
    }

    @Override
    public void execute(String baseDir) {
        sourceMainJava(baseDir, endpointNaming.getShieldFilename(), SHIELD_TEMPLATE);
    }

}
