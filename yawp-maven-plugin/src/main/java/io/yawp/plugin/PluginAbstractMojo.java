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

	protected URLClassLoader createClassLoader() {
		return createClassLoader(new ArrayList<String>());
	}

	protected URLClassLoader createClassLoader(List<String> customClasspathElements) {
		try {
			List<URL> urls = new ArrayList<URL>();

			addURLs(project.getRuntimeClasspathElements(), urls);
			addURLs(customClasspathElements, urls);

			return new URLClassLoader(urls.toArray(new URL[] {}), Thread.currentThread().getContextClassLoader());
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	protected void addURLs(List<String> classpathElements, List<URL> runtimeUrls) throws MalformedURLException {
		List<String> elements = classpathElements;
		for (int i = 0; i < elements.size(); i++) {
			String element = (String) elements.get(i);
			runtimeUrls.add(new File(element).toURI().toURL());
			getLog().debug("Adding to webapp classpath: " + element);
		}
	}

	public RepositorySystem getRepoSystem() {
		return repoSystem;
	}

	protected void setRepoSystem(RepositorySystem repoSystem) {
		this.repoSystem = repoSystem;
	}

	public RepositorySystemSession getRepoSession() {
		return repoSession;
	}

	protected void setRepoSession(RepositorySystemSession repoSession) {
		this.repoSession = repoSession;
	}

	public List<RemoteRepository> getProjectRepos() {
		return projectRepos;
	}

	protected void setProjectRepos(List<RemoteRepository> projectRepos) {
		this.projectRepos = projectRepos;
	}

	public List<RemoteRepository> getPluginRepos() {
		return pluginRepos;
	}

	protected void setPluginRepos(List<RemoteRepository> pluginRepos) {
		this.pluginRepos = pluginRepos;
	}

	public MavenProject getProject() {
		return project;
	}

	protected void setProject(MavenProject project) {
		this.project = project;
	}

}
