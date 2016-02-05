package io.yawp.driver.appengine.pipes;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SourceMarker;

import java.io.Serializable;

import static io.yawp.repository.Yawp.yawp;

public class Payload implements Serializable {

    private static final long serialVersionUID = 8871524196612530063L;

    private String pipeClazzName;

    private String sourceJson;

    private String sinkUri;

    private String sourceMarkerJson;

    private boolean present;

    private transient Class<? extends Pipe> pipeClazz;

    private transient Object source;

    private transient IdRef<?> sinkId;

    private transient SourceMarker sourceMarker;

    private IdRef<?> sourceId;

    public Class<? extends Pipe> getPipeClazz() {
        if (pipeClazz == null) {
            pipeClazz = classForName(pipeClazzName);
        }
        return pipeClazz;
    }

    public void setPipeClazz(Class<? extends Pipe> pipeClazz) {
        this.pipeClazzName = pipeClazz.getName();
    }

    public Object getSource() {
        if (source == null) {
            source = JsonUtils.from(yawp(), sourceJson, ReflectionUtils.getFeatureEndpointClazz(pipeClazz));
        }
        return source;
    }

    public void setSourceJson(Object source) {
        this.sourceJson = JsonUtils.to(source);
    }

    public IdRef<?> getSinkId() {
        if (sinkId == null) {
            sinkId = IdRef.parse(yawp(), sinkUri);
        }
        return sinkId;
    }

    public void setSinkUri(IdRef<?> sinkId) {
        this.sinkUri = sinkId.getUri();
    }

    public SourceMarker getSourceMarker() {
        if (sourceMarker == null) {
            sourceMarker = JsonUtils.from(yawp(), sourceMarkerJson, SourceMarker.class);
        }
        return sourceMarker;
    }

    public void setSourceMarkerJson(SourceMarker sourceMarkerJson) {
        this.sourceMarkerJson = JsonUtils.to(sourceMarkerJson);
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

    public IdRef<?> getSourceId() {
        return new ObjectHolder(getSource()).getId();
    }

    private Class<? extends Pipe> classForName(String pipeClazzName) {
        try {
            return (Class<? extends Pipe>) Class.forName(pipeClazzName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
