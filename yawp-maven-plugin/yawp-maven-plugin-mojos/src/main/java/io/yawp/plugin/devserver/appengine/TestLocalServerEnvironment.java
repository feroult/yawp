package io.yawp.plugin.devserver.appengine;


import com.google.appengine.tools.development.LocalServerEnvironment;
import io.yawp.plugin.devserver.DevServerMojo;

import java.io.File;

public class TestLocalServerEnvironment implements LocalServerEnvironment {

    private DevServerMojo mojo;

    private LocalServerEnvironment delegate;

    TestLocalServerEnvironment(DevServerMojo mojo, LocalServerEnvironment delegate) {
        this.mojo = mojo;
        this.delegate = delegate;
    }

    @Override
    public File getAppDir() {
        return delegate.getAppDir();
    }

    @Override
    public String getAddress() {
        return mojo.getAddress();
    }

    @Override
    public int getPort() {
        return mojo.getPort();
    }

    @Override
    public String getHostName() {
        return delegate.getHostName();
    }

    @Override
    public void waitForServerToStart() throws InterruptedException {
        delegate.waitForServerToStart();
    }

    @Override
    public boolean enforceApiDeadlines() {
        return delegate.enforceApiDeadlines();
    }

    @Override
    public boolean simulateProductionLatencies() {
        return delegate.simulateProductionLatencies();
    }
}