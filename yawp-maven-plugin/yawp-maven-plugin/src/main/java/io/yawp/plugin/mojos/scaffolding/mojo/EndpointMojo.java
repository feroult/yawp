package io.yawp.plugin.mojos.scaffolding.mojo;

import io.yawp.plugin.mojos.scaffolding.EndpointScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "endpoint", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class EndpointMojo extends ScaffolderAbstractMojo {

    @Override
    public void run() {
        EndpointScaffolder scaffolder = new EndpointScaffolder(getLog(), lang, getYawpPackage(), model);
        scaffolder.createTo(baseDir);
    }

}