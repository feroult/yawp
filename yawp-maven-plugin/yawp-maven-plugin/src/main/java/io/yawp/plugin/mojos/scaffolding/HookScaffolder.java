package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class HookScaffolder extends Scaffolder {

    private static final String HOOK_TEMPLATE = "scaffolding/Hook.java.vm";

    public HookScaffolder(Log log, String yawpPackage, String model, String name) {
        super(log, yawpPackage, model);
        naming.hook(name);
    }

    @Override
    public void execute(String baseDir) {
        sourceMain(baseDir, naming.getHookFilename(), HOOK_TEMPLATE);
    }

}
