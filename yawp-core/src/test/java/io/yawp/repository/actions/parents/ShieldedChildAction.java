package io.yawp.repository.actions.parents;

import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.models.parents.ShieldedChild;

public class ShieldedChildAction extends Action<ShieldedChild> {

    @PUT("collection")
    public void collection(IdRef<Parent> parentId) {
    }

    @PUT("single")
    public void single(IdRef<ShieldedChild> id) {
    }

}
