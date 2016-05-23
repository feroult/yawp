package io.yawp.plugin.base;

import org.apache.maven.artifact.DependencyResolutionRequiredException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassLoaderBuilder {

    private List<URL> urls;

    public ClassLoaderBuilder() {
        urls = new ArrayList<URL>();
    }

    public URLClassLoader build() {
        return new URLClassLoader(urls.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
    }

    public void addRuntime(PluginAbstractMojo mojo) {
        try {
            if (mojo.getProject() == null) {
                return;
            }
            add(mojo.getProject().getRuntimeClasspathElements());
        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(List<String> classpathElements) {
        try {
            List<String> elements = classpathElements;
            for (int i = 0; i < elements.size(); i++) {
                String element = elements.get(i);
                urls.add(new File(element).toURI().toURL());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
