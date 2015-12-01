package io.yawp.plugin.sync;

import io.yawp.commons.utils.ServiceLookup;
import io.yawp.driver.api.Driver;
import io.yawp.plugin.base.ClassLoaderBuilder;
import io.yawp.plugin.base.PluginAbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.net.URLClassLoader;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "sync", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class SyncMojo extends PluginAbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Driver driver = ServiceLookup.lookup(Driver.class, runtimeClassLoader());
        driver.helpers().sync();
    }

    private URLClassLoader runtimeClassLoader() {
        ClassLoaderBuilder builder = new ClassLoaderBuilder();
        builder.addRuntime(this);
        return builder.build();
    }

}
