package io.yawp.repository.pipes;

import io.yawp.repository.models.ObjectModel;
import io.yawp.repository.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class Pump<T> {

    private Class<T> clazz;

    private int defaultBatchSize;

    private List<T> objects = new ArrayList<>();

    private int objectsIndex = 0;

    private List<QueryBuilder<T>> queries = new ArrayList<>();

    private int queryIndex = 0;

    private String cursor;

    public Pump(Class<T> clazz, int batchSize) {
        this.clazz = clazz;
        this.defaultBatchSize = batchSize;
    }

    public void addObject(T object) {
        objects.add(object);
    }

    public void addObjects(List<T> newObjects) {
        objects.addAll(newObjects);
    }

    public void addQuery(QueryBuilder<T> query) {
        if (!query.hasPreOrder()) {
            ObjectModel model = new ObjectModel(clazz);
            query.order(model.getIdFieldName());
        }
        queries.add(query);
    }

    public List<T> more() {
        if (hasMoreObjects()) {
            List<T> list = moreFromList();
            if (list.size() < defaultBatchSize && hasMoreQueries()) {
                list.addAll(moreFromQuery(defaultBatchSize - list.size()));
            }
            return list;
        }
        return moreFromQuery(defaultBatchSize);
    }

    private List<T> moreFromList() {
        List<T> list = new ArrayList<>();

        int fromIndex = objectsIndex;
        int toIndex = objectsIndex + defaultBatchSize;

        if (toIndex >= objects.size()) {
            toIndex = objects.size();
        }

        objectsIndex = toIndex;

        list.addAll(objects.subList(fromIndex, toIndex));
        return list;
    }

    private List<T> moreFromQuery(int batchSize) {
        QueryBuilder<T> q = queries.get(queryIndex);
        if (cursor != null) {
            q.cursor(cursor);
        }
        q.limit(batchSize);
        List<T> list = q.list();
        cursor = q.getCursor();
        if (list.size() < batchSize) {
            queryIndex++;
            cursor = null;
            if (hasMoreQueries()) {
                list.addAll(moreFromQuery(defaultBatchSize - list.size()));
            }
        }
        return list;
    }

    public boolean hasMore() {
        return hasMoreObjects() || hasMoreQueries();
    }

    private boolean hasMoreQueries() {
        return queryIndex < queries.size();
    }

    private boolean hasMoreObjects() {
        return objectsIndex < objects.size();
    }
}
