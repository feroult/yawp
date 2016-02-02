package io.yawp.driver.appengine.pipes;

import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.VersionMarker;

public class Payload {

    private Class<? extends Pipe> pipeClazz;

    private Object source;

    private IdRef<?> sinkId;

    private VersionMarker versionMarker;

    public void setPipeClazz(Class<? extends Pipe> pipeClazz) {
        this.pipeClazz = pipeClazz;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public void setSinkId(IdRef<?> sinkId) {
        this.sinkId = sinkId;
    }

    public void setVersionMarker(VersionMarker versionMarker) {
        this.versionMarker = versionMarker;
    }
}
