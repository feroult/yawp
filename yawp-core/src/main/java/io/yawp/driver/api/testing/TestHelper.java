package io.yawp.driver.api.testing;

import io.yawp.repository.Repository;

import java.util.concurrent.TimeUnit;

public interface TestHelper {

    void init(Repository r);

    void setUp();

    void tearDown();

    void awaitAsync(long timeout, TimeUnit unit);
}
