package io.yawp.commons.utils;

import io.yawp.driver.api.testing.TestHelper;
import io.yawp.driver.api.testing.TestHelperFactory;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Feature;
import io.yawp.repository.Repository;
import io.yawp.repository.RepositoryFeatures;

import org.junit.After;
import org.junit.Before;

public class EndpointTestCase extends Feature {

	private static RepositoryFeatures features;

	private TestHelper helper;

	static {
		features = new EndpointScanner("io.yawp").scan();
	}

	@Before
	public void setUp() {
		yawp = Repository.r().setFeatures(features);
		helper = testHelperDriver(yawp);
		helper.setUp();
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
}
