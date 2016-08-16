package io.yawp.commons.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceLookup {

    public static final String SERVICES_PATH = "META-INF/services/";

    public static Map<Class<?>, Class<?>> cache = new HashMap<>();

    private ServiceLookup() {
    }

    public static <T> T lookup(Class<T> clazz) {
        try {
            Class serviceClazz;
            if (!cache.containsKey(clazz)) {
                serviceClazz = findServiceClazz(clazz);
                cache.put(clazz, serviceClazz);
            } else {
                serviceClazz = cache.get(clazz);
            }
            return (T) serviceClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Class findServiceClazz(Class<T> clazz) throws IOException, ClassNotFoundException {
        ResourceFinder finder = new ResourceFinder(SERVICES_PATH);
        List<Class> clazzes = finder.findAllImplementations(clazz);

        if (clazzes.size() == 0) {
            throw new RuntimeException(String.format("No service implementation for %s.", clazz.getSimpleName()));
        }

        return clazzes.get(0);
    }

}
