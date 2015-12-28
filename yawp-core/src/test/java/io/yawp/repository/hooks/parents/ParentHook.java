package io.yawp.repository.hooks.parents;

import io.yawp.repository.hooks.parents.AbstractHook;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.QueryBuilder;

public class ParentHook extends AbstractHook<Parent> {

    @Override
    public void beforeQuery(QueryBuilder<Parent> q) {
        if (!isOnRequest() || !requestContext.hasParam("name")) {
            return;
        }

        q.where("name", "=", requestContext.getParam("name"));
    }

}
