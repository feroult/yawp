package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.c;
import io.yawp.commons.http.annotation.GET;
import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;
import io.yawp.repository.models.basic.facades.ShieldedObjectFacades.AmyFacade;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class ObjectShield extends Shield<ShieldedObject> {

	@Override
	public void always() {
		allow(isJim());
		allow(isAmy()).facade(AmyFacade.class);
	}

	@Override
	public void index(IdRef<?> parentId) {
		allow(isKurt()).where(c("stringValue", "=", "ok"));
	}

	@Override
	public void show(IdRef<ShieldedObject> id) {
		allow(isRobert());
		allow(isId100(id));
		allow(isKurt()).where(c("stringValue", "=", "ok"));
	}

	@Override
	public void create(List<ShieldedObject> objects) {
		allow(isRequestWithValidObjects(objects));
		allow(isKurt()).where(c("stringValue", "=", "ok"));
		allow(isJanis()).where(c("stringValue", "=", "ok-for-janis"));
	}

	@Override
	public void update(IdRef<ShieldedObject> id, ShieldedObject object) {
		allow(isRobert());
		allow(isId100(id));
		allow(isRequestWithValidObject(object));
		allow(isKurt()).where(c("stringValue", "=", "ok"));
		allow(isJanis()).where(c("stringValue", "=", "ok-for-janis"));
	}

	@Override
	public void destroy(IdRef<ShieldedObject> id) {
		allow(isId100(id));
		allow(isJanis()).where(c("stringValue", "=", "ok-for-janis"));
	}

	@Override
	public void custom() {
		allow(isRobert());
	}

	@PUT("something")
	public void something(IdRef<ShieldedObject> id) {
		allow(isJanis()).where(c("stringValue", "=", "ok-for-janis"));
	}

	@PUT("anotherthing")
	public void anotherthing(IdRef<ShieldedObject> id, Map<String, String> params) {
		allow(isId100(id));
		allow(params.containsKey("x") && params.get("x").equals("ok"));
	}

	@GET("collection")
	public void collection() {
		allow();
	}

	private boolean isRobert() {
		return is("robert@rock.com");
	}

	private boolean isJim() {
		return is("jim@rock.com");
	}

	private boolean isKurt() {
		return is("kurt@rock.com");
	}

	private boolean isJanis() {
		return is("janis@rock.com");
	}

	private boolean isAmy() {
		return is("amy@rock.com");
	}

	private boolean is(String email) {
		User currentUser = UserServiceFactory.getUserService().getCurrentUser();
		return currentUser != null && currentUser.getEmail().equals(email);
	}

	private boolean isId100(IdRef<ShieldedObject> id) {
		return id.asLong().equals(100l);
	}

	private boolean isRequestWithValidObject(ShieldedObject object) {
		if (object == null) {
			return false;
		}
		return isRequestWithValidObjects(Arrays.asList(object));
	}

	private boolean isRequestWithValidObjects(List<ShieldedObject> objects) {
		if (!requestHasAnyObject()) {
			return false;
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
