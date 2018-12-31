package io.yawp.repository.hooks;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

public class AfterQueryIdsObject<T> {
    private QueryBuilder<T> query;
    private List<IdRef<T>> ids;

    public AfterQueryIdsObject(QueryBuilder<T> query, List<IdRef<T>> ids) {
        this.query = query;
        this.ids = ids;
    }

    public QueryBuilder<T> getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder<T> query) {
        this.query = query;
    }

    public List<IdRef<T>> getIds() {
        return ids;
    }

    public void setIds(List<IdRef<T>> ids) {
        this.ids = ids;
    }
}
