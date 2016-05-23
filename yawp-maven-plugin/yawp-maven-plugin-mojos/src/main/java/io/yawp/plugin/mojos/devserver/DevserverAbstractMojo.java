package io.yawp.plugin.mojos.devserver;

import io.yawp.plugin.devserver.ShutdownMonitor;
import io.yawp.plugin.mojos.base.PluginAbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class DevserverAbstractMojo extends PluginAbstractMojo {

    @Parameter(property = "yawp.shutdownPort", defaultValue = ShutdownMonitor.DEFAULT_PORT)
    private String shutdownPort;

    public int getShutdownPort() {
        return Integer.valueOf(shutdownPort);
    }

}
