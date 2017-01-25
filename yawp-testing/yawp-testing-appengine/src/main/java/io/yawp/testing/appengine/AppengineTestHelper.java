package io.yawp.testing.appengine;

import com.google.appengine.tools.development.testing.*;
import io.yawp.driver.api.testing.TestHelper;
import io.yawp.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppengineTestHelper implements TestHelper {

    private LocalServiceTestHelper helper;

    @Override
    public void init(Repository r) {
    }

    @Override
    public void setUp() {

        helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(),
                createDatastoreService(),
                createTaskQueueTestConfig(),
                new LocalSearchServiceTestConfig(),
                new LocalModulesServiceTestConfig(),
                new LocalMemcacheServiceTestConfig(),
                new LocalURLFetchServiceTestConfig());

        Map<String, Object> envs = new HashMap<>();
        helper.setEnvAttributes(envs);
        helper.setUp();
    }

    protected LocalDatastoreServiceTestConfig createDatastoreService() {
        return new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0);
    }

    protected LocalTaskQueueTestConfig createTaskQueueTestConfig() {
        LocalTaskQueueTestConfig config = new LocalTaskQueueTestConfig();
        config.setShouldCopyApiProxyEnvironment(true);
        config.setDisableAutoTaskExecution(false);
        config.setCallbackClass(TestingTaskQueueCallback.class);
        return config;
    }

    @Override
    public void tearDown() {
        helper.tearDown();
    }

    @Override
    public void awaitAsync(long timeout, TimeUnit unit) {
        AsyncHelper.awaitAsync(timeout, unit);
    }

    public void login(String username, String domain) {
        login(username, domain, false);
    }

    public void login(String username, String domain, boolean isAdmin) {
        helper.setEnvAuthDomain(domain);
        helper.setEnvEmail(username + "@" + domain);
        helper.setEnvIsLoggedIn(true);
        helper.setEnvIsAdmin(isAdmin);
    }

    public void logout() {
        helper.setEnvIsLoggedIn(false);
    }

}
