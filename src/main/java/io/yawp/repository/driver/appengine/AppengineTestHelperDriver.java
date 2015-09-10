package io.yawp.repository.driver.appengine;

import io.yawp.repository.driver.api.TestHelperDriver;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class AppengineTestHelperDriver implements TestHelperDriver {

	private LocalServiceTestHelper helper;

	@Override
	public void setUp() {
		helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(), new LocalDatastoreServiceTestConfig());
		helper.setEnvIsLoggedIn(true);
		Map<String, Object> envs = new HashMap<String, Object>();
		helper.setEnvAttributes(envs);
		helper.setEnvAuthDomain("localhost");
		helper.setEnvEmail("test@localhost");
		helper.setUp();
	}

	@Override
	public void tearDown() {
		helper.tearDown();
	}

}
