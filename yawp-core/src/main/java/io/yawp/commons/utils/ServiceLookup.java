package io.yawp.commons.utils;

import java.util.List;

public class ServiceLookup {

    public static final String SERVICES_PATH = "META-INF/services/";

    public static <T> T lookup(Class<T> clazz) {
        ResourceFinder finder = new ResourceFinder(SERVICES_PATH);
        return lookup(finder, clazz);
    }

    public static <T> T lookup(Class<T> clazz, ClassLoader classLoader) {
        ResourceFinder finder = new ResourceFinder(SERVICES_PATH, classLoader);
        return lookup(finder, clazz);
    }

    private static <T> T lookup(ResourceFinder finder, Class<T> clazz) {
        try {
            List<Class> clazzes = finder.findAllImplementations(clazz);

            if(clazzes.size() == 0) {
                throw new RuntimeException(String.format("No service implementation for %s.", clazz.getSimpleName()));
            }

            return (T) clazzes.get(0).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
