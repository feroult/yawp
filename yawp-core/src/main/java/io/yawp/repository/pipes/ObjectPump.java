package io.yawp.repository.pipes;

import io.yawp.repository.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ObjectPump<T> extends Pump<T> {

    private List<QueryBuilder<T>> queries = new ArrayList<>();

    public ObjectPump(int batchSize) {
        super(batchSize);
    }

    @Override
    protected List<T> executeQueryAt(int queryIndex) {
        return queries.get(queryIndex).list();
    }

    @Override
    protected QueryBuilder<?> getQueryAt(int queryIndex) {
        return queries.get(queryIndex);
    }

    @Override
    public void addQuery(QueryBuilder<?> query) {
        queries.add((QueryBuilder<T>) query);
    }

    @Override
    protected int getQueriesSize() {
        return queries.size();
    }
}
