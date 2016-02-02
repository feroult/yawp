package io.yawp.driver.appengine.pipes;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.VersionMarker;

import java.io.Serializable;

public class Payload implements Serializable {

    private static final long serialVersionUID = 8871524196612530063L;

    private Class<? extends Pipe> pipeClazz;

    private String sourceJson;

    private String sinkUri;

    private String versionMarkerJson;

    private transient Object source;

    private transient IdRef<?> sinkId;

    private transient VersionMarker versionMarker;

    public void setPipeClazz(Class<? extends Pipe> pipeClazz) {
        this.pipeClazz = pipeClazz;
    }

    public void setSource(Object source) {
        this.sourceJson = JsonUtils.to(source);
    }

    public void setSinkId(IdRef<?> sinkId) {
        this.sinkUri = sinkId.getUri();
    }

    public void setVersionMarkerJson(VersionMarker versionMarker) {
        this.versionMarkerJson = JsonUtils.to(versionMarker);
    }

    public void init(Repository r) {
        this.source = JsonUtils.from(r, sourceJson, ReflectionUtils.getFeatureEndpointClazz(pipeClazz));
        this.sinkId = IdRef.parse(r, sinkUri);
        this.versionMarker = JsonUtils.from(r, versionMarkerJson, VersionMarker.class);
    }
}
