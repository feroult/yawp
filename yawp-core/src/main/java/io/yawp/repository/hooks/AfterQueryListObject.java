package io.yawp.repository.hooks;

import io.yawp.repository.query.QueryBuilder;

import java.util.List;

public class AfterQueryListObject<T> {
    private QueryBuilder<T> query;
    private List<T> list;

    public AfterQueryListObject(QueryBuilder<T> query, List<T> list) {
        this.query = query;
        this.list = list;
    }

    public QueryBuilder<T> getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder<T> query) {
        this.query = query;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
