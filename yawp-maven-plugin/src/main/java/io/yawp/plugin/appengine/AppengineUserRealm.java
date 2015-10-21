package io.yawp.plugin.appengine;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.security.UserRealm;

import com.google.appengine.api.users.User;

public class AppengineUserRealm implements UserRealm {

	private static final String REALM_NAME = "YAWP! DevServer Realm";

	public static final String USER_ROLE = "*";

	public static final String ADMIN_ROLE = "admin";

	Map<String, User> users = new HashMap<String, User>();

	@Override
	public String getName() {
		return REALM_NAME;
	}

	@Override
	public Principal getPrincipal(String username) {
		if (!isUserLoggedIn(username)) {
			return null;
		}
		return getPrincipal(users.get(username));
	}

	@Override
	public Principal authenticate(String username, Object credentials, Request request) {
		users.put(username, (User) credentials);
		return getPrincipal(username);
	}

	@Override
	public boolean reauthenticate(Principal user) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isUserInRole(Principal user, String role) {
		if (role.equals(ADMIN_ROLE)) {

		}
		return false;
	}

	@Override
	public void disassociate(Principal user) {
		// TODO Auto-generated method stub

	}

	@Override
	public Principal pushRole(Principal user, String role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal popRole(Principal user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout(Principal user) {
		// TODO Auto-generated method stub

	}

	private boolean isUserLoggedIn(String username) {
		return users.containsKey(username);
	}

	private Principal getPrincipal(User user) {
		// TODO Auto-generated method stub
		return null;
	}

}
