package io.yawp.repository.shields.parents;

import static io.yawp.repository.query.condition.Condition.c;

import io.yawp.commons.http.annotation.PUT;
import io.yawp.commons.utils.TestLoginManager;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.models.parents.ShieldedChild;
import io.yawp.repository.shields.Shield;

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
        allow(isJanis()).where("parent->name", "=", "ok-for-janis");
    }

    @PUT("single")
    public void single(IdRef<ShieldedChild> id) {
        allow(isId100(id));
        allow(isJanis()).where(c("parent->name", "=", "ok-for-janis"));
    }

    private boolean isId100(IdRef<?> id) {
        return id.asLong().equals(100l);
    }

    private boolean isJanis() {
        return is("janis");
    }

    private boolean is(String username) {
        return TestLoginManager.isLogged(username);
        // return TestLoginManager.getLoggedUsername().equals(username);
        // User currentUser =
        // UserServiceFactory.getUserService().getCurrentUser();
        // return currentUser != null && currentUser.getEmail().equals(email);
    }
}
