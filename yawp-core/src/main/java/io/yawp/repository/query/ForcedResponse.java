package io.yawp.repository.query;

import io.yawp.repository.IdRef;

import java.util.List;

public class ForcedResponse<T> {
    private T byId;
    private List<T> list;
    private List<IdRef<T>> ids;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getById() {
        return byId;
    }

    public void setById(T byId) {
        this.byId = byId;
    }

    public List<IdRef<T>> getIds() {
        return ids;
    }

    public void setIds(List<IdRef<T>> ids) {
        this.ids = ids;
    }
}
