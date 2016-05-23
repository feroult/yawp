package io.yawp.plugin.devserver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.yawp.plugin.base.ClassLoaderBuilder;
import org.apache.maven.plugin.logging.Log;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;
import org.mortbay.util.Scanner;
import org.mortbay.util.Scanner.DiscreteListener;
import org.mortbay.xml.XmlConfiguration;

public class WebAppContextHelper {

    protected DevServerMojo mojo;

    protected WebAppContext webapp;

    public WebAppContextHelper(DevServerMojo mojo) {
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
        webapp.setClassLoader(createClassLoader());
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

    protected URLClassLoader createClassLoader() {
        ClassLoaderBuilder builder = new ClassLoaderBuilder();
        builder.addRuntime(mojo);
        builder.add(getCustomClasspathElements());
        return builder.build();
    }
    
}
