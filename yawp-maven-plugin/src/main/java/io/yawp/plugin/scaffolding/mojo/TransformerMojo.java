package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.TransformerScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "transformer", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class TransformerMojo extends ScaffolderAbstractMojo {

    @Parameter(property = "name", required = true)
    protected String name;

    @Override
    public void run() throws MojoExecutionException {
        TransformerScaffolder scaffolder = new TransformerScaffolder(getLog(), getYawpPackage(), model, name);
        scaffolder.createTo(baseDir);
    }

}