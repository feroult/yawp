package io.yawp.driver.postgresql.configuration;

import io.yawp.commons.utils.Environment;
import io.yawp.commons.utils.ResourceFinder;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class InitialContextSetup implements InitialContextFactory {

    private static Context context;

    public static String envDataSourceName() {
        return String.format("jdbc/yawp_%s", Environment.getOrDefault());
    }

    private static class Xpto extends InitialContext {

        Map<String, Object> bindings = new HashMap<String, Object>();

        public Xpto() throws NamingException {
            super(true);
        }

        @Override
        public void bind(String name, Object obj) throws NamingException {
            bindings.put(name, obj);
        }

        @Override
        public Object lookup(String name) throws NamingException {
            return bindings.get(name);
        }

    }

    static {
        try {
            context = new Xpto();

            context.bind("java:comp/env", context);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return context;
    }

    public static void bind(String name, Object obj) {
        try {
            context.bind(name, obj);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void unregister() {
        System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
    }


    public static void configure(String resourceUri) {
        String path = getPath(resourceUri);
        configure(new File(path));
    }

    public static void configure(File file) {
        if (alreadyRegisteredInitialContext()) {
            return;
        }

        JettyConfiguration configuration = new JettyConfiguration(file.getAbsolutePath());
        DataSourceInfo dsInfo = configuration.getDatasourceInfo(Environment.getOrDefault());
        bind(envDataSourceName(), dsInfo.buildDatasource());
    }

    private static boolean alreadyRegisteredInitialContext() {
        if (System.getProperty(Context.INITIAL_CONTEXT_FACTORY) != null) {
            return true;
        }

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextSetup.class.getName());
        System.setProperty("java.naming.factory.url.pkgs", InitialContextSetup.class.getPackage().getName());
        return false;
    }

    private static String getPath(String resourceUri) {
        try {
            URL url = new ResourceFinder().find(resourceUri);
            return url.getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
