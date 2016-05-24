package io.yawp.plugin.devserver;

public class MojoWrapper {

    private String env;

    private String baseDir;

    private String appDir;

    private String address;

    private Integer port;

    private Integer fullScanSeconds;

    private String hotDeployDir;

    private Integer shutdownPort;

    private boolean appengine;

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
