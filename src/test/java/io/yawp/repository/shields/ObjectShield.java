package io.yawp.repository.shields;

import io.yawp.repository.actions.annotations.PUT;
import io.yawp.repository.models.basic.ShieldedObject;
import io.yawp.utils.HttpVerb;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class ObjectShield extends Shield<ShieldedObject> {

	@Override
	public void always() {
		allow(isJim());
	}

	@Override
	public void show() {
		allow(isJane());
		allow(isId100());
	}

	@Override
	public void create() {
		allow(isRequestWithValidObject());
	}

	@Override
	public void update() {
		allow(isJane());
		allow(isId100());
		allow(isRequestWithValidObject());
	}

	@Override
	public void destroy() {
		allow(isId100());
	}

	@Override
	public void custom() {
		allow(isJane());
	}

	@PUT("anotherthing")
	public void anotherthing() {
		allow();
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
