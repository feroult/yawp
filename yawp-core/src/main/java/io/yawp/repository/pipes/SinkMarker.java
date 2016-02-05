package io.yawp.repository.pipes;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

@Endpoint(kind = "__yawp_pipes_sink_markers")
public class SinkMarker {

    @Id
    private IdRef<SinkMarker> id;

    @ParentId
    private IdRef<?> parentId;

    private Long version = 1L;

    private boolean present;

    public void setId(IdRef<SinkMarker> id) {
        this.id = id;
    }

    public void setParentId(IdRef<?> parentId) {
        this.parentId = parentId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

}
