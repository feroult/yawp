package io.yawp.repository.hooks.parents;

import io.yawp.repository.hooks.BeforeQueryObject;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.QueryBuilder;

public class ParentHook extends Hook<Parent> {

    @Override
    public void beforeQuery(BeforeQueryObject<Parent> obj) {
        if (!isOnRequest() || !requestContext.hasParam("name")) {
            return;
        }

        obj.getQuery().where("name", "=", requestContext.getParam("name"));
    }

}
