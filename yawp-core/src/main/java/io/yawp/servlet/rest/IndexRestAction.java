package io.yawp.servlet.rest;

import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOptions;

import java.util.List;

public class IndexRestAction extends RestAction {

    public IndexRestAction() {
        super("index");
    }

    @Override
    public void shield() {
        shield.protectIndex();
    }

    @Override
    public List<?> action() {
        QueryBuilder<?> query = query();

        if (id != null) {
            query.from(id);
        }

        if (params.containsKey(QUERY_OPTIONS)) {
            query.options(QueryOptions.parse(params.get(QUERY_OPTIONS)));
        }

        if (hasTransformer()) {
            return query.transform(getTransformerName()).list();
        }

        if (hasShieldCondition()) {
            query.and(shield.getCondition());
        }

        List<?> objects = query.list();
        applyGetFacade(objects);
        return objects;
    }

}
