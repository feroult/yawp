package io.yawp.commons.utils;

import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Feature;
import io.yawp.repository.Repository;
import io.yawp.repository.RepositoryFeatures;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class EndpointTestCase extends Feature {

	private static final String LOGGED_USER_ID = "10";

	private static RepositoryFeatures features;

	private LocalServiceTestHelper helper;

	@BeforeClass
	public static void bootEndpoint() {
		features = new EndpointScanner("io.yawp").scan();
	}

	@Before
	public void setupHelper() {
		helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(), new LocalDatastoreServiceTestConfig());
		helper.setEnvIsLoggedIn(true);
		Map<String, Object> envs = new HashMap<String, Object>();
		envs.put("com.google.appengine.api.users.UserService.user_id_key", LOGGED_USER_ID);
		helper.setEnvAttributes(envs);
		helper.setEnvAuthDomain("localhost");
		helper.setEnvEmail("test@localhost");
		helper.setUp();
	}

	protected void login(String username, String domain) {
		helper.setEnvAuthDomain(domain);
		helper.setEnvEmail(username + "@" + domain);
	}

	@Before
	public void setupRepository() {
		yawp = Repository.r(LOGGED_USER_ID).setFeatures(features);
	}

	@After
	public void tearDownHelper() {
		helper.tearDown();
	}
}
