package io.yawp.repository.pipes;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;
import io.yawp.repository.annotations.Text;
import io.yawp.repository.models.ObjectHolder;

import static io.yawp.repository.Yawp.yawp;

@Endpoint(kind = "__yawp_pipes_sink_markers")
public class SinkMarker {

    @Id
    private IdRef<SinkMarker> id;

    @ParentId
    private IdRef<?> parentId;

    private String sourceClazzName;

    @Text
    private String sourceJson;

    private Long version = 1L;

    private boolean present;

    private transient Object source;

    public void setId(IdRef<SinkMarker> id) {
        this.id = id;
    }

    public IdRef<?> getParentId() {
        return parentId;
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

    public Object getSource() {
        if (source == null) {
            source = JsonUtils.from(yawp(), sourceJson, ReflectionUtils.clazzForName(sourceClazzName));
        }
        return source;
    }

    public IdRef<?> getSourceId() {
        Object source = getSource();
        ObjectHolder objectHolder = new ObjectHolder(source);
        return objectHolder.getId();
    }

    public void setSourceJson(Class<?> endpointClazz, String sourceJson) {
        this.sourceClazzName = endpointClazz.getName();
        this.sourceJson = sourceJson;
        this.source = null;
    }
}
