package io.yawp.repository.pipes;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

@Endpoint(kind = "__yawp_pipes_source_markers")
public class SourceMarker {

    @Id
    private IdRef<SourceMarker> id;

    @ParentId
    private IdRef<?> parentId;

    private Long version = 1L;

    public void setId(IdRef<SourceMarker> id) {
        this.id = id;
    }

    public IdRef<SourceMarker> getId() {
        return id;
    }


    public void setParentId(IdRef<?> parentId) {
        this.parentId = parentId;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void increment() {
        version++;
    }

    public Long getVersion() {
        return version;
    }

    public IdRef<?> getParentId() {
        return parentId;
    }
}
