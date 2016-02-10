package io.yawp.driver.mock;

import io.yawp.driver.api.testing.TestHelper;
import io.yawp.repository.Repository;

import java.util.concurrent.TimeUnit;

public class MockTestHelper implements TestHelper {

    @Override
    public void init(Repository r) {
    }

    @Override
    public void setUp() {
        MockStore.clear();
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void awaitAsync(long timeout, TimeUnit unit) {

    }

}
