package io.yawp.plugin.mojos.scaffolding;

import org.apache.maven.plugin.logging.Log;

public class TransformerScaffolder extends Scaffolder {

    private static final String TRANSFORMER_TEMPLATE = "scaffolding/Transformer.java.vm";

    public TransformerScaffolder(Log log, String yawpPackage, String model, String name) {
        super(log, yawpPackage, model);
        naming.transformer(name);
    }

    @Override
    public void execute(String baseDir) {
        sourceMain(baseDir, naming.getTransformerFilename(), TRANSFORMER_TEMPLATE);
    }

}
