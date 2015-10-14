package io.yawp.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

public abstract class PluginAbstractMojo extends AbstractMojo {

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

}
