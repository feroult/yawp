package io.yawp.repository.shields;

import io.yawp.commons.http.annotation.GET;
import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;

import java.util.List;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class ObjectShield extends Shield<ShieldedObject> {

	@Override
	public void always() {
		allow(isJim());
	}

	@Override
	public void show(IdRef<ShieldedObject> id) {
		allow(isJane());
		allow(isId100(id));
	}

	@Override
	public void create(ShieldedObject object, List<ShieldedObject> objects) {
		allow(isRequestWithValidObject(object, objects));
	}

	@Override
	public void update(IdRef<ShieldedObject> id, ShieldedObject object) {
		allow(isJane());
		allow(isId100(id));
		allow(isRequestWithValidObject(object, null));
	}

	@Override
	public void destroy(IdRef<ShieldedObject> id) {
		allow(isId100(id));
	}

	@Override
	public void custom() {
		allow(isJane());
	}

	@PUT("anotherthing")
	public void anotherthing(IdRef<ShieldedObject> id) {
		if (isId100(id)) {
			allow();
		}
	}

	@GET("something-over-collection")
	public void somethingOverCollection() {
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

	private boolean isId100(IdRef<ShieldedObject> id) {
		return id.asLong().equals(100l);
	}

	private boolean isRequestWithValidObject(ShieldedObject object, List<ShieldedObject> objects) {
		if (!requestHasAnyObject()) {
			return false;
		}

		if (!requestHasObjectArray()) {
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
}
