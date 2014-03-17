package endpoint.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import endpoint.models.actions.RepositoryActions;
import endpoint.models.hooks.RepositoryHooks;

public class GAETest {

	private static final String LOGGED_USER_ID = "10";
	private LocalServiceTestHelper helper;

	@Before
	public void setup() {
		setUserLoggedIn(LOGGED_USER_ID);

		RepositoryActions.scan("endpoint");
		RepositoryHooks.scan("endpoint");
	}

	protected void setUserLoggedIn(String userId) {
		setupHelper(userId);
		setupNamespace(userId);
	}

	private void setupHelper(String userId) {
		helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(), new LocalDatastoreServiceTestConfig());
		helper.setEnvIsLoggedIn(userId != null);
		if (userId != null) {
			Map<String, Object> envs = new HashMap<String, Object>();
			envs.put("com.google.appengine.api.users.UserService.user_id_key", userId);
			helper.setEnvAttributes(envs);
			helper.setEnvAuthDomain("localhost");
			helper.setEnvEmail("test@localhost");
		}
		helper.setUp();
	}

	private void setupNamespace(String userId) {
		NamespaceManager.set(userId != null ? userId : "");
	}

	@After
	public void tearDownHelper() {
		NamespaceManager.set("");
		helper.tearDown();
	}

}
