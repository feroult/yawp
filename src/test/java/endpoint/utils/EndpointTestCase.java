package endpoint.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import endpoint.repository.EndpointScanner;
import endpoint.repository.Repository;
import endpoint.repository.RepositoryFeatures;

public class EndpointTestCase {

	private static final String LOGGED_USER_ID = "10";

	private static RepositoryFeatures features;

	private LocalServiceTestHelper helper;

	protected Repository r;

	@BeforeClass
	public static void bootEndpoint() {
		features = new EndpointScanner("endpoint").scan();
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

	@Before
	public void setupRepository() {
		r = Repository.r(LOGGED_USER_ID).setFeatures(features);
	}

	@After
	public void tearDownHelper() {
		helper.tearDown();
	}
}
