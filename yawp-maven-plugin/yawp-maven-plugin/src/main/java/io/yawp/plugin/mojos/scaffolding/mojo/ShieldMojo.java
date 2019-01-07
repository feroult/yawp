package io.yawp.plugin.mojos.scaffolding.mojo;

import io.yawp.plugin.mojos.scaffolding.ShieldScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "shield", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ShieldMojo extends ScaffolderAbstractMojo {

    @Override
    public void run() throws MojoExecutionException {
        ShieldScaffolder scaffolder = new ShieldScaffolder(getLog(), lang, getYawpPackage(), model);
        scaffolder.createTo(baseDir);
    }

}