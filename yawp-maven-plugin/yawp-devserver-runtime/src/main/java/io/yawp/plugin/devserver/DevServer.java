package io.yawp.plugin.devserver;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.plugin.devserver.appengine.AppengineWebAppContextHelper;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;

import java.io.IOException;

public class DevServer {

    private MojoWrapper mojo;

    protected Server server;

    private WebAppContextHelper helper;

    public DevServer(String mojoJson) {
        this.mojo = JsonUtils.from(null, mojoJson, MojoWrapper.class);
    }

    public void run() {
        initHelper();
        startServer();
        startShutdownMonitor();
    }

    private void initHelper() {
        if (mojo.isAppengine()) {
            this.helper = new AppengineWebAppContextHelper(mojo);
        } else {
            this.helper = new WebAppContextHelper(mojo);
        }
    }

    protected void startServer() {
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
        connector.setHost(mojo.getAddress());
        connector.setPort(mojo.getPort());
        connector.setSoLingerTime(0);
        connector.open();
        return connector;
    }

    protected void startShutdownMonitor() {
        try {
            ShutdownMonitor monitor = new ShutdownMonitor(mojo, mojo.getShutdownPort());
            monitor.start();
            monitor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }


}
