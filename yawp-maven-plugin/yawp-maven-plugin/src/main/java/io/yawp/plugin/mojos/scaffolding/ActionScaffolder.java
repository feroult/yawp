package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class ActionScaffolder extends Scaffolder {

    private static final String ACTION_TEMPLATE = "scaffolding/Action.java.vm";

    public ActionScaffolder(Log log, String yawpPackage, String model, String name) {
        super(log, yawpPackage, model);
        naming.action(name);
    }

    @Override
    public void execute(String baseDir) {
        sourceMain(baseDir, naming.getActionFilename(), ACTION_TEMPLATE);
    }

}
