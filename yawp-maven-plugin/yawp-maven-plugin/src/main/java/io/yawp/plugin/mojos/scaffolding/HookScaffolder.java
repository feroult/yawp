package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class HookScaffolder extends Scaffolder {

    private static final String HOOK_TEMPLATE = "scaffolding/Hook.java.vm";

    public HookScaffolder(Log log, String yawpPackage, String model, String name) {
        super(log, yawpPackage, model);
        endpointNaming.hook(name);
    }

    @Override
    public void execute(String baseDir) {
        sourceMainJava(baseDir, endpointNaming.getHookFilename(), HOOK_TEMPLATE);
    }

}
