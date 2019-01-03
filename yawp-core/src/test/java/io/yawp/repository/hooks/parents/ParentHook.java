package io.yawp.repository.hooks.parents;

import io.yawp.repository.hooks.Hook;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.QueryBuilder;

public class ParentHook extends Hook<Parent> {

    @Override
    public void beforeQuery(QueryBuilder<Parent> q) {
        if (!isOnRequest() || !requestContext.hasParam("name")) {
            return;
        }

        q.where("name", "=", requestContext.getParam("name"));
    }

}
