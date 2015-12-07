package io.yawp.driver.postgresql;

import io.yawp.commons.utils.Environment;
import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.configuration.WebConfiguration;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.RepositoryFeatures;

import javax.xml.validation.Schema;
import java.io.File;
import java.util.Set;

public class PGHelpersDriver implements HelpersDriver {

    private SchemaSynchronizer schemaSynchronizer = new SchemaSynchronizer();

    @Override
    public void deleteAll() {
        schemaSynchronizer.truncateAll();
    }

    @Override
    public void sync() {
        InitialContextSetup.configure(getJettyConfigFile());
        schemaSynchronizer.sync(scanEndpointClazzes());
    }

    private Set<Class<?>> scanEndpointClazzes() {
        WebConfiguration webConfiguration = new WebConfiguration(getWebConfigFile());
        RepositoryFeatures features = new EndpointScanner(webConfiguration.getPackagePrefix()).scan();
        return features.getEndpointClazzes();
    }

    private File getJettyConfigFile() {
        return new File(String.format("%s/src/main/webapp/WEB-INF/jetty-env.xml", Environment.getBaseDir()));
    }

    private String getWebConfigFile() {
        return String.format("%s/src/main/webapp/WEB-INF/web.xml", Environment.getBaseDir());
    }

}
