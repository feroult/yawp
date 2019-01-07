package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class ShieldScaffolder extends Scaffolder {

    private static final String SHIELD_TEMPLATE = "scaffolding/${lang}/Shield.${ext}.vm";

    public ShieldScaffolder(Log log, String lang, String yawpPackage, String model) {
        super(log, lang, yawpPackage, model);
    }

    @Override
    public void execute(String baseDir) {
        sourceMain(baseDir, naming.getShieldFilename(), naming.parsePath(SHIELD_TEMPLATE));
    }

}
