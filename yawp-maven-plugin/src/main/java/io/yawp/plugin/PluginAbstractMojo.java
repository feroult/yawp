package io.yawp.plugin;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

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

    @Parameter(property = "yawp.dir", defaultValue = "${basedir}")
    protected String baseDir;

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
}
