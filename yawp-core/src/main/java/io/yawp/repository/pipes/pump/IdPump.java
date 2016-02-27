package io.yawp.repository.pipes.pump;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class IdPump<T> extends Pump<IdRef<T>> {

    private List<QueryBuilder<T>> queries = new ArrayList<>();

    public IdPump(int batchSize) {
        super(batchSize);
    }

    @Override
    protected List<IdRef<T>> executeQueryAt(int queryIndex) {
        return queries.get(queryIndex).ids();
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
