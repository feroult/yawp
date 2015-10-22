package io.yawp.plugin.appengine;

import java.security.Principal;

import com.google.appengine.api.users.User;

public class AppengineUser {

	private User user;

	private boolean admin;

	private Principal principal;

	public AppengineUser(final User user, boolean admin) {
		this.user = user;
		this.admin = admin;

		this.principal = new Principal() {
			@Override
			public String getName() {
				return user.getEmail();
			}
		};
	}

	public User getUser() {
		return user;
	}

	public boolean isAdmin() {
		return admin;
	}

	public Principal getPrincipal() {
		return principal;
	}

}
