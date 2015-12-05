package io.yawp.testing.postgresql;

import io.yawp.commons.utils.Environment;
import io.yawp.commons.utils.ResourceFinder;
import io.yawp.driver.api.testing.TestHelper;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;
import io.yawp.repository.Repository;

import java.io.File;
import java.io.IOException;

public class PGTestHelper implements TestHelper {

    public static final String INTERNAL_TEST_JETTY_ENV_XML = "configuration/jetty-env.xml";
    private Repository r;

    @Override
    public void init(Repository r) {
        this.r = r;
        configureInitialContext();
        resetTables();
    }

    private void configureInitialContext() {
        Environment.set("test");
        if (isUserTest()) {
            InitialContextSetup.configure(getUserJettyConfigFile());
        } else {
            InitialContextSetup.configure(INTERNAL_TEST_JETTY_ENV_XML);
        }
    }

    private void resetTables() {
        SchemaSynchronizer.recreate("public");
        SchemaSynchronizer.sync(r.getFeatures().getEndpointClazzes());
    }

    @Override
    public void setUp() {
        SchemaSynchronizer.truncateAll();
    }

    @Override
    public void tearDown() {
    }

    public boolean isUserTest() {
        try {
            new ResourceFinder().find(INTERNAL_TEST_JETTY_ENV_XML);
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    private File getUserJettyConfigFile() {
        return new File(String.format("%s/src/main/webapp/WEB-INF/jetty-env.xml", getBaseDir()));
    }

    private String getBaseDir() {
        if(Environment.getBaseDir() != null) {
            return Environment.getBaseDir();
        }
        return PGTestHelper.class.getResource("/").getFile() + "../../";
    }

}
