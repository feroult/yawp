package io.yawp.repository.hooks;

import io.yawp.repository.query.QueryBuilder;

public class AfterQueryFetchObject<T> {
    private QueryBuilder<T> query;
    private T element;

    public AfterQueryFetchObject(QueryBuilder<T> query, T element) {
        this.query = query;
        this.element = element;
    }

    public QueryBuilder<T> getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder<T> query) {
        this.query = query;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }
}
