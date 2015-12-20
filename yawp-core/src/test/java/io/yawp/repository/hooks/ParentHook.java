package io.yawp.repository.hooks;

import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.QueryBuilder;

public class ParentHook extends Hook<Parent> {

    @Override
    public void beforeQuery(QueryBuilder<Parent> q) {
        super.beforeQuery(q);
    }

}
