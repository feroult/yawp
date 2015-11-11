package io.yawp.testing.appengine;

import io.yawp.driver.api.testing.TestHelper;
import io.yawp.repository.Repository;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class AppengineTestHelper implements TestHelper {

	private LocalServiceTestHelper helper;

	@Override
	public void init(Repository r) {
	}

	@Override
	public void setUp() {
		helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(), new LocalDatastoreServiceTestConfig());
		Map<String, Object> envs = new HashMap<String, Object>();
		helper.setEnvAttributes(envs);
		helper.setUp();
	}

	@Override
	public void tearDown() {
		helper.tearDown();
	}

	public void login(String username, String domain) {
		login(username, domain, false);
	}

	public void login(String username, String domain, boolean isAdmin) {
		helper.setEnvAuthDomain(domain);
		helper.setEnvEmail(username + "@" + domain);
		helper.setEnvIsLoggedIn(true);
	}

	public void logout() {
		helper.setEnvIsLoggedIn(false);
	}

}
