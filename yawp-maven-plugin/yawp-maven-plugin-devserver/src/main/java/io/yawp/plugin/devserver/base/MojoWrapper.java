package io.yawp.plugin.devserver.base;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

public class MojoWrapper {

    private AbstractMojo mojo;

    private ClassLoader classLoader;

    private RepositorySystem repoSystem;

    private RepositorySystemSession repoSession;

    private List<RemoteRepository> projectRepos;

    private List<RemoteRepository> pluginRepos;

    private MavenProject project;

    private String env;

    private String baseDir;

    private String appDir;

    private String address;

    private Integer port;

    private Integer fullScanSeconds;

    private String hotDeployDir;

    private Integer shutdownPort;

    private boolean appengine;

    public Log getLog() {
        return mojo.getLog();
    }

    public AbstractMojo getMojo() {
        return mojo;
    }

    public void setMojo(AbstractMojo mojo) {
        this.mojo = mojo;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public RepositorySystem getRepoSystem() {
        return repoSystem;
    }

    public void setRepoSystem(RepositorySystem repoSystem) {
        this.repoSystem = repoSystem;
    }

    public RepositorySystemSession getRepoSession() {
        return repoSession;
    }

    public void setRepoSession(RepositorySystemSession repoSession) {
        this.repoSession = repoSession;
    }

    public List<RemoteRepository> getProjectRepos() {
        return projectRepos;
    }

    public void setProjectRepos(List<RemoteRepository> projectRepos) {
        this.projectRepos = projectRepos;
    }

    public List<RemoteRepository> getPluginRepos() {
        return pluginRepos;
    }

    public void setPluginRepos(List<RemoteRepository> pluginRepos) {
        this.pluginRepos = pluginRepos;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getFullScanSeconds() {
        return fullScanSeconds;
    }

    public void setFullScanSeconds(Integer fullScanSeconds) {
        this.fullScanSeconds = fullScanSeconds;
    }

    public String getHotDeployDir() {
        return hotDeployDir;
    }

    public void setHotDeployDir(String hotDeployDir) {
        this.hotDeployDir = hotDeployDir;
    }

    public Integer getShutdownPort() {
        return shutdownPort;
    }

    public void setShutdownPort(Integer shutdownPort) {
        this.shutdownPort = shutdownPort;
    }

    public boolean isAppengine() {
        return appengine;
    }

    public void setAppengine(boolean appengine) {
        this.appengine = appengine;
    }

}
