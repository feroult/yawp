package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class PipeScaffolder extends Scaffolder {

    private static final String PIPE_TEMPLATE = "scaffolding/Pipe.java.vm";

    public PipeScaffolder(Log log, String yawpPackage, String model, String name, String sink) {
        super(log, yawpPackage, model);
        endpointNaming.pipe(name);
        endpointNaming.pipeSink(sink);
    }

    @Override
    public void execute(String baseDir) {
        sourceMainJava(baseDir, endpointNaming.getPipeFilename(), PIPE_TEMPLATE);
    }

}
