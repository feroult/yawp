package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.c;
import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.models.parents.ShieldedChild;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class ChildShield extends Shield<ShieldedChild> {

	@Override
	public void index(IdRef<?> parentId) {
		allow(isId100(parentId));
	}

	@Override
	public void show(IdRef<ShieldedChild> id) {
		allow(isId100(id));
	}

	@PUT("collection")
	public void collection(IdRef<Parent> parentId) {
		allow(isId100(parentId));

		// TODO: this is not the best approach
		allow(isJanis()).where(c("name", "=", "ok-for-janis"));
	}

	@PUT("single")
	public void single(IdRef<ShieldedChild> id) {
		allow(isId100(id));
		//allow(isJanis()).where(c("name", "=", "ok-for-janis"));
	}

	private boolean isId100(IdRef<?> id) {
		return id.asLong().equals(100l);
	}

	private boolean isJanis() {
		return is("janis@rock.com");
	}

	private boolean is(String email) {
		User currentUser = UserServiceFactory.getUserService().getCurrentUser();
		return currentUser != null && currentUser.getEmail().equals(email);
	}
}
