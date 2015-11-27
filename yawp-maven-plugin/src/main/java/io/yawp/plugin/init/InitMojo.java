package io.yawp.plugin.init;

import io.yawp.plugin.PluginAbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "init")
public class InitMojo extends PluginAbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isAppengine()) {
            getLog().info("Nothing to init for the Appengine driver...");
            return;
        }


    }

}
