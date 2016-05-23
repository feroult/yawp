package io.yawp.plugin.devserver;

import org.apache.maven.plugins.annotations.Parameter;

import io.yawp.plugin.base.PluginAbstractMojo;

public abstract class DevserverAbstractMojo extends PluginAbstractMojo {

    @Parameter(property = "yawp.shutdownPort", defaultValue = ShutdownMonitor.DEFAULT_PORT)
    private String shutdownPort;

    public int getShutdownPort() {
        return Integer.valueOf(shutdownPort);
    }

}
