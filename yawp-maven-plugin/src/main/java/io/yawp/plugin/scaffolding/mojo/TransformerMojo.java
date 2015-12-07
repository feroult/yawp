package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.TransformerScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "transformer")
public class TransformerMojo extends ScaffolderAbstractMojo {

    @Parameter(property = "name", required = true)
    protected String name;

    @Override
    public void run() throws MojoExecutionException {
        TransformerScaffolder scaffolder = new TransformerScaffolder(getLog(), yawpPackage, model, name);
        scaffolder.createTo(baseDir);
    }

}