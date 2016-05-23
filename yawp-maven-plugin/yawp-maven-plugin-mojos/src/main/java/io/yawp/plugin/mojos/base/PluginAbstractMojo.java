package io.yawp.plugin.mojos.base;

import io.yawp.commons.utils.Environment;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.net.URLClassLoader;
import java.util.List;

public abstract class PluginAbstractMojo extends AbstractMojo {

    private static final String YAWP_GROUP_ID = "io.yawp";

    private static final String YAWP_ARTIFACT_ID = "yawp";

    @Component
    protected RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}")
    protected RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}")
    protected List<RemoteRepository> projectRepos;

    @Parameter(defaultValue = "${project.remotePluginRepositories}")
    protected List<RemoteRepository> pluginRepos;

    @Parameter(defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(property = "env", defaultValue = "development")
    protected String env;

    @Parameter(property = "yawp.dir", defaultValue = "${basedir}")
    protected String baseDir;

    @Parameter(property = "yawp.appDir", defaultValue = "${basedir}/src/main/webapp")
    protected String appDir;

    public abstract void run() throws MojoExecutionException, MojoFailureException;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Environment.set(env);
        run();
    }

    public RepositorySystem getRepoSystem() {
        return repoSystem;
    }

    public RepositorySystemSession getRepoSession() {
        return repoSession;
    }

    public List<RemoteRepository> getProjectRepos() {
        return projectRepos;
    }

    public List<RemoteRepository> getPluginRepos() {
        return pluginRepos;
    }

    public MavenProject getProject() {
        return project;
    }

    protected boolean isAppengine() {
        if (project.getGroupId().equals(YAWP_GROUP_ID) && project.getArtifactId().equals(YAWP_ARTIFACT_ID)) {
            return true;
        }

        for (Dependency dependency : project.getDependencies()) {
            if (dependency.getGroupId().equals(YAWP_GROUP_ID) && dependency.getArtifactId().equals(YAWP_ARTIFACT_ID)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isPostgreSQL() {
        return !isAppengine();
    }

    public String getAppDir() {
        return appDir;
    }

    public String getEnv() {
        return env;
    }

    public String getBaseDir() {
        return baseDir;
    }

    protected URLClassLoader configureRuntimeClassLoader() {
        ClassLoaderBuilder builder = new ClassLoaderBuilder();
        builder.addRuntime(this);
        URLClassLoader classLoader = builder.build();
        Thread.currentThread().setContextClassLoader(classLoader);
        return classLoader;
    }
}
