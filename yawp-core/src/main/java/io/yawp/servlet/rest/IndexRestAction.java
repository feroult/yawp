package io.yawp.servlet.rest;

import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexRestAction extends RestAction {

    public IndexRestAction() {
        super("index");
    }

    @Override
    public void shield() {
        shield.protectIndex();
    }

    @Override
    public Object action() {
        QueryBuilder<?> query = query();

        if (id != null) {
            query.from(id);
        }

        boolean returnCursor = false;
        if (params.containsKey(QUERY_OPTIONS)) {
            QueryOptions options = QueryOptions.parse(params.get(QUERY_OPTIONS));
            query.options(options);
            returnCursor = options.returnCursor();
        }

        if (hasShieldCondition()) {
            query.and(shield.getWhere());
        }

        List<?> objects = list(query);

        if (returnCursor) {
            Map<String, Object> result = new HashMap<>();
            result.put("results", objects);
            result.put("cursor", query.getCursor());
            return result;
        }
        
        return objects;
    }

    private List<?> list(QueryBuilder<?> query) {
        List<?> objects;
        if (hasTransformer()) {
            objects = query.transform(getTransformerName()).list();
        } else {
            objects = query.list();
            applyGetFacade(objects);
        }
        return objects;
    }

}
