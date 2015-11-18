package io.yawp.driver.api.testing;

import io.yawp.repository.Repository;

public interface TestHelper {

    void init(Repository r);

    void setUp();

    void tearDown();

}
