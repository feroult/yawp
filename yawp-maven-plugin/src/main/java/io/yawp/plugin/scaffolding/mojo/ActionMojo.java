package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.ActionScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "action", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ActionMojo extends ScaffolderAbstractMojo {

    @Parameter(property = "name", required = true)
    protected String name;

    @Override
    public void run() throws MojoExecutionException {
        ActionScaffolder scaffolder = new ActionScaffolder(getLog(), getYawpPackage(), model, name);
        scaffolder.createTo(baseDir);
    }

}