package io.yawp.repository.pipes;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

@Endpoint(kind = "__yawp_version_markers")
public class VersionMarker {

    @Id
    private IdRef<VersionMarker> id;

    @ParentId
    private IdRef<?> parentId;

    private Long version = 1L;

    public void setId(IdRef<VersionMarker> id) {
        this.id = id;
    }

    public void setParentId(IdRef<?> parentId) {
        this.parentId = parentId;
    }

    public void increment() {
        version++;
    }

    public Long getVersion() {
        return version;
    }
}
