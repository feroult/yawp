package io.yawp.driver.api.testing;

import io.yawp.repository.Repository;

public interface TestHelper {

	public void init(Repository r);

	public void setUp();

	public void tearDown();

}
