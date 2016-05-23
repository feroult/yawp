package io.yawp.plugin.mojos.scaffolding.mojo;

import io.yawp.plugin.mojos.scaffolding.HookScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "hook", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class HookMojo extends ScaffolderAbstractMojo {

    @Parameter(property = "name", required = true)
    protected String name;

    @Override
    public void run() throws MojoExecutionException {
        HookScaffolder scaffolder = new HookScaffolder(getLog(), getYawpPackage(), model, name);
        scaffolder.createTo(baseDir);
    }

}