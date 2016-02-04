package io.yawp.driver.appengine.pipes;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.VersionMarker;

import java.io.Serializable;

import static io.yawp.repository.Yawp.yawp;

public class Payload implements Serializable {

    private static final long serialVersionUID = 8871524196612530063L;

    private Class<? extends Pipe> pipeClazz;

    private String sourceJson;

    private String sinkUri;

    private String versionMarkerJson;

    private boolean present;

    private transient Object source;

    private transient IdRef<?> sinkId;

    private transient VersionMarker versionMarker;

    public Class<? extends Pipe> getPipeClazz() {
        return pipeClazz;
    }

    public void setPipeClazz(Class<? extends Pipe> pipeClazz) {
        this.pipeClazz = pipeClazz;
    }

    public Object getSource() {
        if (source == null) {
            source = JsonUtils.from(yawp(), sourceJson, ReflectionUtils.getFeatureEndpointClazz(pipeClazz));
        }
        return source;
    }

    public void setSource(Object source) {
        this.sourceJson = JsonUtils.to(source);
    }

    public IdRef<?> getSinkId() {
        if (sinkId == null) {
            sinkId = IdRef.parse(yawp(), sinkUri);
        }
        return sinkId;
    }

    public void setSinkId(IdRef<?> sinkId) {
        this.sinkUri = sinkId.getUri();
    }

    public VersionMarker getVersionMarker() {
        if (versionMarker == null) {
            versionMarker = JsonUtils.from(yawp(), versionMarkerJson, VersionMarker.class);
        }
        return versionMarker;
    }

    public void setVersionMarkerJson(VersionMarker versionMarker) {
        this.versionMarkerJson = JsonUtils.to(versionMarker);
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getSinkUri() {
        return sinkUri;
    }
}
