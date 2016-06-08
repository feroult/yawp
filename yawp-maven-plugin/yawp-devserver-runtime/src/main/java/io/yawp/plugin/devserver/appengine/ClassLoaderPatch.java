package io.yawp.plugin.devserver.appengine;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Deprecated
public class ClassLoaderPatch {
    private static final Class<?>[] parameters = new Class[]{URL.class};

    private ClassLoaderPatch() {}

    public static void addFiles(List<String> paths) {
        for (String path : paths) {
            addFile(path);
        }
    }

    public static void addFile(String path) {
        addFile(new File(path));
    }

    public static void addFile(File file) {
        try {
            addURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addURL(URL url) {
        try {
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<?> clazz = URLClassLoader.class;

            Method method = clazz.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(classLoader, new Object[]{url});

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}