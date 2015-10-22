package io.yawp.plugin.appengine;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.security.UserRealm;

public class AppengineUserRealm implements UserRealm {

	private static final String REALM_NAME = "YAWP! DevServer Realm";

	public static final String USER_ROLE = "*";

	public static final String ADMIN_ROLE = "admin";

	public static final String[] ROLES = new String[] { USER_ROLE, ADMIN_ROLE };

	Map<String, AppengineUser> users = new HashMap<String, AppengineUser>();

	@Override
	public String getName() {
		return REALM_NAME;
	}

	@Override
	public Principal getPrincipal(String username) {
		if (!users.containsKey(username)) {
			return null;
		}
		return users.get(username).getPrincipal();
	}

	@Override
	public Principal authenticate(String username, Object credentials, Request request) {
		AppengineUser user = (AppengineUser) credentials;
		users.put(username, user);
		return user.getPrincipal();
	}

	@Override
	public boolean reauthenticate(Principal user) {
		return true;
	}

	@Override
	public boolean isUserInRole(Principal user, String role) {
		if (role.equals(ADMIN_ROLE)) {
			return users.get(user.getName()).isAdmin();
		}
		return role.equals(USER_ROLE);
	}

	@Override
	public void disassociate(Principal user) {
	}

	@Override
	public Principal pushRole(Principal user, String role) {
		return users.get(user.getName()).getPrincipal();
	}

	@Override
	public Principal popRole(Principal user) {
		return users.get(user.getName()).getPrincipal();
	}

	@Override
	public void logout(Principal user) {
		users.remove(user.getName());
	}

}
