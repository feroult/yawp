package io.yawp.driver.postgresql;

import io.yawp.commons.utils.Environment;
import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;

import javax.xml.validation.Schema;
import java.io.File;

public class PGHelpersDriver implements HelpersDriver {

    @Override
    public void deleteAll() {
        SchemaSynchronizer.truncateAll();
    }

    @Override
    public void sync() {
        configureInitialContext();
    }

    private void configureInitialContext() {
        InitialContextSetup.configure(getConfigFile());
        //SchemaSynchronizer.sync();
    }

    private File getConfigFile() {
        return new File(String.format("%s/src/main/webapp/WEB-INF/jetty-env.xml", Environment.getBaseDir()));
    }

}
