package io.yawp.plugin.devserver;

import io.yawp.plugin.devserver.base.MojoWrapper;
import org.apache.maven.plugin.logging.Log;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;
import org.mortbay.util.Scanner;
import org.mortbay.util.Scanner.DiscreteListener;
import org.mortbay.xml.XmlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebAppContextHelper {

    protected MojoWrapper mojo;

    protected WebAppContext webapp;

    public WebAppContextHelper(MojoWrapper mojo) {
        this.mojo = mojo;
    }

    public WebAppContext createWebApp() {
        webapp = createWebAppContext();
        webapp.setDefaultsDescriptor(getWebDefaultXml());

        configureCustom();
        configureClassloader();
        configureHotDeploy();
        return webapp;
    }

    protected void configureCustom() {
    }

    protected WebAppContext createWebAppContext() {
        try {
            Resource jettyEnv = Resource.newResource(String.format("%s/WEB-INF/jetty-env.xml", mojo.getAppDir()));
            XmlConfiguration conf = new XmlConfiguration(jettyEnv.getInputStream());
            WebAppContext webapp = (WebAppContext) conf.configure();
            webapp.setWar(mojo.getAppDir());
            System.setProperty("java.naming.factory.url.pkgs", "org.mortbay.naming");
            System.setProperty("java.naming.factory.initial", "org.mortbay.naming.InitialContextFactory");
            return webapp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getWebDefaultXml() {
        return "/webdefault.xml";
    }

    private void configureClassloader() {
        webapp.setClassLoader(mojo.getClassLoader());
    }

    protected List<String> getCustomClasspathElements() {
        return new ArrayList<String>();
    }

    protected void configureHotDeploy() {
        getLog().info("HotDeploy scanner: " + mojo.getHotDeployDir());
        Scanner scanner = new Scanner();
        scanner.setScanInterval(mojo.getFullScanSeconds());
        scanner.setScanDirs(Arrays.asList(new File(mojo.getHotDeployDir())));
        scanner.addListener(new DiscreteListener() {

            @Override
            public void fileChanged(String filename) throws Exception {
                fileAdded(filename);
            }

            @Override
            public void fileAdded(String filename) throws Exception {
                if (!webapp.isStarted()) {
                    return;
                }
                getLog().info(filename + " updated, reloading the webapp!");
                restart(webapp);
            }

            @Override
            public void fileRemoved(String filename) throws Exception {
            }
        });
        scanner.scan();
        scanner.start();
    }

    private Log getLog() {
        return mojo.getLog();
    }

    private void restart(WebAppContext webapp) throws Exception {
        webapp.stop();
        configureClassloader();
        webapp.start();
    }

}
