package io.yawp.driver.postgresql;

import io.yawp.commons.utils.Environment;
import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.configuration.WebConfiguration;
import io.yawp.driver.postgresql.tools.DatabaseSynchronizer;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.RepositoryFeatures;

import java.io.File;
import java.util.Set;

public class PGHelpersDriver implements HelpersDriver {

    private DatabaseSynchronizer dbSynchronizer = new DatabaseSynchronizer();

    @Override
    public void deleteAll() {
        dbSynchronizer.truncateAll();
    }

    @Override
    public void sync() {
        InitialContextSetup.configure(getJettyConfigFile());
        dbSynchronizer.sync(scanEndpointClazzes());
    }

    private Set<Class<?>> scanEndpointClazzes() {
        WebConfiguration webConfiguration = new WebConfiguration(getWebConfigFile());
        RepositoryFeatures features = new EndpointScanner(webConfiguration.getPackagePrefix()).scan();
        return features.getEndpointClazzes();
    }

    private File getJettyConfigFile() {
        return new File(String.format("%s/WEB-INF/jetty-env.xml", Environment.getAppDir()));
    }

    private String getWebConfigFile() {
        return String.format("%s/WEB-INF/web.xml", Environment.getAppDir());
    }

}
