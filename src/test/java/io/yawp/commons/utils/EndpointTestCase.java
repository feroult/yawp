package io.yawp.commons.utils;

import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Feature;
import io.yawp.repository.Repository;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.driver.api.RepositoryDriverFactory;
import io.yawp.repository.driver.api.TestHelperDriver;

import org.junit.After;
import org.junit.Before;

public class EndpointTestCase extends Feature {

	private static final String LOGGED_USER_ID = "10";

	private static RepositoryFeatures features;

	private TestHelperDriver helper;

	static {
		features = new EndpointScanner("io.yawp").scan();
	}

	@Before
	public void setUp() {
		yawp = Repository.r(LOGGED_USER_ID).setFeatures(features);
		helper = testHelperDriver(yawp);
		helper.setUp();
	}

	private TestHelperDriver testHelperDriver(Repository r) {
		return RepositoryDriverFactory.getRepositoryDriver(r).helpers().tests();
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
