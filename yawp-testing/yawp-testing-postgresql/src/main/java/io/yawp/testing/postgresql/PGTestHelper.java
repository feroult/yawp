package io.yawp.testing.postgresql;

import io.yawp.commons.utils.Environment;
import io.yawp.commons.utils.ResourceFinder;
import io.yawp.driver.api.testing.TestHelper;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.tools.DatabaseSynchronizer;
import io.yawp.repository.Repository;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PGTestHelper implements TestHelper {

    private static final String INTERNAL_TEST_JETTY_ENV_XML = "configuration/jetty-env.xml";

    private Repository r;

    DatabaseSynchronizer dbSynchronizer;

    @Override
    public void init(Repository r) {
        this.r = r;
        configureInitialContext();
        initDatabase();
    }

    private void configureInitialContext() {
        if (isUserTest()) {
            InitialContextSetup.configure(getUserJettyConfigFile());
        } else {
            InitialContextSetup.configure(INTERNAL_TEST_JETTY_ENV_XML);
        }
    }

    private void initDatabase() {
        dbSynchronizer = new DatabaseSynchronizer();
        dbSynchronizer.sync(r.getFeatures().getEndpointClazzes());
    }

    @Override
    public void setUp() {
        dbSynchronizer.truncateAll();
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void awaitAsync(long timeout, TimeUnit unit) {

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
        return new File(String.format("%s/WEB-INF/jetty-env.xml", getAppDir()));
    }

    private String getAppDir() {
        if (Environment.getAppDir() != null) {
            return Environment.getAppDir();
        }
        return PGTestHelper.class.getResource("/").getFile() + "../../src/main/webapp";
    }

}
