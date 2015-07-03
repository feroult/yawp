package io.yawp.repository.shields;

import io.yawp.repository.models.basic.ShieldedObject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class ObjectShield extends Shield<ShieldedObject> {

	@Override
	protected void always() {
		allow(isJim());
	}

	@Override
	protected void show() {
		allow(isJane());
		allow(isId100());
	}

	@Override
	protected void create() {
		allow(isJane());
		allow(isRouteWithValidObject());
	}

	@Override
	protected void update() {
		allow(isJane());
		allow(isId100());
	}

	@Override
	protected void destroy() {
		allow(isId100());
	}

	@Override
	protected void custom() {
		allow(isJane());
	}

	private boolean isJane() {
		return is("jane@rock.com");
	}

	private boolean isJim() {
		return is("jim@rock.com");
	}

	private boolean is(String email) {
		User currentUser = UserServiceFactory.getUserService().getCurrentUser();
		return currentUser != null && currentUser.getEmail().equals(email);
	}

	private boolean isId100() {
		return id.asLong().equals(100l);
	}

	private boolean isRouteWithValidObject() {
		if (object == null) {
			return false;
		}
		return object.getStringValue().equals("valid route with object");
	}

}
