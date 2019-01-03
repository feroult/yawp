package io.yawp.repository.cache;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/cached_entity")
public class CachedEntity {

    @Id
    private IdRef<CachedEntity> id;
    private String text;

    public CachedEntity() {
    }

    public CachedEntity(String text) {
        this.text = text;
    }

    public IdRef<CachedEntity> getId() {
        return id;
    }

    public void setId(IdRef<CachedEntity> id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
