package io.yawp.repository.shields;

import java.util.List;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class ObjectShield extends Shield<ShieldedObject> {

	@Override
	protected void always() {
		allow(isJim());
	}

	@Override
	protected void show(IdRef<ShieldedObject> id) {
		allow(isJane());
		allow(isId100(id));
	}

	@Override
	protected void create(ShieldedObject object) {
		allow(isJane());


		if (!isArray()) {
			allow(isRouteWithValidObject(object));
		}
	}

	protected void create(List<ShieldedObject> objects) {
		//allow(isJane());

		// loop
	}


	@Override
	protected void update(IdRef<ShieldedObject> id, ShieldedObject object) {
		allow(isJane());
		allow(isId100(id));
	}

	@Override
	protected void destroy(IdRef<ShieldedObject> id) {
		allow(isId100(id));
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

	private boolean isId100(IdRef<ShieldedObject> id) {
		return id.asLong().equals(100l);
	}

	private boolean isRouteWithValidObject(ShieldedObject object) {
		if (object == null) {
			return false;
		}
		return object.getStringValue().equals("valid route with object");
	}

}
