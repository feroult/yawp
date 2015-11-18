package io.yawp.plugin.devserver;

import io.yawp.plugin.devserver.appengine.AppengineWebAppContextHelper;

import java.io.IOException;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DevServerMojo extends DevserverAbstractMojo {

    private static final String YAWP_GROUP_ID = "io.yawp";

    private static final String YAWP_ARTIFACT_ID = "yawp";

    @Parameter(property = "yawp.address", defaultValue = "0.0.0.0")
    private String address;

    @Parameter(property = "yawp.port", defaultValue = "8080")
    private String port;

    @Parameter(property = "yawp.fullScanSeconds", defaultValue = "3")
    private String fullScanSeconds;

    @Parameter(property = "yawp.hotDeployDir", defaultValue = "${basedir}/target/classes")
    protected String hotDeployDir;

    @Parameter(property = "yawp.appDir", defaultValue = "${basedir}/src/main/webapp")
    protected String appDir;

    protected Server server;

    private WebAppContextHelper helper;

    public void execute() throws MojoExecutionException {
        initHelper();
        startServer();
        startShutdownMonitor();
    }

    private void initHelper() {
        if (isYawpAppengine()) {
            this.helper = new AppengineWebAppContextHelper(this);
        } else {
            this.helper = new WebAppContextHelper(this);
        }
    }

    protected void startServer() {
        getLog().info("Starting webserver at: " + appDir);

        try {
            server = new Server();
            server.addConnector(createConnector());
            server.setHandler(helper.createWebApp());
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        // no better solution to avoid exceptions when trying to stop jetty
        // server thread
        // the best options would be server.stop()
        System.exit(0);
    }

    private SelectChannelConnector createConnector() throws IOException {
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setHost(getAddress());
        connector.setPort(getPort());
        connector.setSoLingerTime(0);
        connector.open();
        return connector;
    }

    private boolean isYawpAppengine() {
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

    protected void startShutdownMonitor() {
        try {
            ShutdownMonitor monitor = new ShutdownMonitor(this, getShutdownPort());
            monitor.start();
            monitor.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHotDeployDir() {
        return hotDeployDir;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return Integer.valueOf(port);
    }

    public int getFullScanSeconds() {
        return Integer.valueOf(fullScanSeconds);
    }

    public String getAppDir() {
        return appDir;
    }
}