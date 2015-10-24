package io.yawp.plugin.appengine;

import java.security.Principal;

public class AppengineUser {

	private String username;

	private boolean admin;

	private Principal principal;

	public AppengineUser(String cookie) {
		// amy@domain.com:true:141224291207813574210
		String[] split = cookie.split(":");
		this.username = split[0];
		this.admin = Boolean.valueOf(split[1]);
	}

	public String getUsername() {
		return username;
	}

	public boolean isAdmin() {
		return admin;
	}

	public Principal getPrincipal() {
		if (principal == null) {
			this.principal = new Principal() {
				@Override
				public String getName() {
					return username;
				}
			};
		}
		return principal;
	}

}
