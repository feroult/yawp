package io.yawp.repository.pipes;

import io.yawp.repository.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class Pump<T> {

    private int batchSize;

    private List<T> objects = new ArrayList<>();

    private int objectsIndex = 0;

    private List<QueryBuilder<T>> queries = new ArrayList<>();

    private String cursor;

    public Pump(int batchSize) {
        this.batchSize = batchSize;
    }

    public void addObject(T object) {
        objects.add(object);
    }

    public void addObjects(List<T> newObjects) {
        objects.addAll(newObjects);
    }

    public void addQuery(QueryBuilder<T> query) {
        queries.add(query);
    }

    public List<T> more() {
        if (objects.size() > 0) {
            return moreFromList();
        }
        return moreFromQuery();
    }

    private List<T> moreFromList() {
        int fromIndex = objectsIndex;
        int toIndex = objectsIndex + batchSize;

        if (toIndex >= objects.size()) {
            toIndex = objects.size();
        }

        objectsIndex = toIndex;

        return objects.subList(fromIndex, toIndex);
    }

    private List<T> moreFromQuery() {
        QueryBuilder<T> q = queries.get(0);
        if (cursor != null) {
            q.cursor(cursor);
        }
        q.limit(batchSize);
        List<T> list = q.list();
        cursor = q.getCursor();
        return list;
    }
}
