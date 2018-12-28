package io.yawp.commons.utils;

import io.yawp.driver.api.DriverNotImplementedException;
import io.yawp.driver.api.testing.TestHelper;
import io.yawp.driver.api.testing.TestHelperFactory;
import io.yawp.repository.features.Feature;
import io.yawp.repository.Repository;
import io.yawp.repository.Yawp;
import io.yawp.servlet.cache.Cache;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.TimeUnit;

public class EndpointTestCase extends Feature {

    private TestHelper helper;

    @Before
    public void setUp() {
        Environment.setIfEmpty(Environment.DEFAULT_TEST_ENVIRONMENT);

        yawp = Yawp.yawp();
        helper = testHelperDriver(yawp);
        helper.setUp();
        Cache.clearAll();
    }

    private TestHelper testHelperDriver(Repository r) {
        return TestHelperFactory.getTestHelper(r);
    }

    protected void login(String username) {
        TestLoginManager.login(username);
    }

    @After
    public void tearDownHelper() {
        helper.tearDown();
        TestLoginManager.logout();
    }

    protected boolean pipesDriverNotImplemented() {
        // TODO: pipes - remove this
        try {
            yawp.driver().pipes();
            return false;
        } catch (DriverNotImplementedException e) {
            return true;
        }
    }

    protected void awaitAsync(long timeout, TimeUnit unit) {
        helper.awaitAsync(timeout, unit);
    }

}
