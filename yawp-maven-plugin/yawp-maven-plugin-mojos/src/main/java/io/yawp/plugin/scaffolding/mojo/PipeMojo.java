package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.PipeScaffolder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "pipe", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PipeMojo extends ScaffolderAbstractMojo {

    @Parameter(property = "name", required = true)
    protected String name;

    @Parameter(property = "sink", required = true)
    protected String sink;

    @Override
    public void run() throws MojoExecutionException {
        PipeScaffolder scaffolder = new PipeScaffolder(getLog(), getYawpPackage(), model, name, sink);
        scaffolder.createTo(baseDir);
    }

}