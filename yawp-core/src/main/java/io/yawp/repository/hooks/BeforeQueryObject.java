package io.yawp.repository.hooks;

import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryType;

public class BeforeQueryObject<T> {
    private QueryBuilder<T> query;
    private QueryType type;

    public BeforeQueryObject(QueryBuilder<T> query, QueryType type) {
        this.query = query;
        this.type = type;
    }

    public QueryBuilder<T> getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder<T> query) {
        this.query = query;
    }

    public QueryType getType() {
        return type;
    }

    public void setType(QueryType type) {
        this.type = type;
    }
}
