package io.yawp.repository.shields;

import io.yawp.repository.models.basic.ShieldedObject;
import io.yawp.utils.HttpVerb;

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
		allow(isRequestWithValidObject());
	}

	@Override
	protected void update() {
		allow(isJane());
		allow(isId100());
		allow(isRequestWithValidObject());
	}

	@Override
	protected void destroy() {
		allow(isId100());
	}

	@Override
	protected void custom() {
		allow(isJane() && isSomethingAction());
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

	private boolean isRequestWithValidObject() {
		if (!requestHasObject()) {
			return false;
		}

		if (!isArray()) {
			return isValidObject(object);
		}

		for (ShieldedObject objectInList : objects) {
			if (!isValidObject(objectInList)) {
				return false;
			}
		}

		return true;
	}

	private boolean isValidObject(ShieldedObject object) {
		if (object.getStringValue() == null) {
			return false;
		}
		return object.getStringValue().equals("valid object");
	}

	private boolean isSomethingAction() {
		return isAction(HttpVerb.PUT, "something");
	}
}
