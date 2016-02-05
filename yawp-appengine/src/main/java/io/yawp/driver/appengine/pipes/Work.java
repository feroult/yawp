package io.yawp.driver.appengine.pipes;

import com.google.common.hash.Sink;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;

import static io.yawp.repository.Yawp.yawp;

@Endpoint(kind = "__yawp_pipe_works")
public class Work {

    @Id
    private IdRef<Work> id;

    @Index(normalize = false)
    private final String indexHash;

    @Json
    private final Payload payload;

    public Work(String indexHash, Payload payload) {
        this.indexHash = indexHash;
        this.payload = payload;
    }

    public <T, S> void flow(Object sink) {
        Pipe<T, S> pipe = createPipeInstance();
        if (payload.isPresent()) {
            pipe.flux((T) payload.getSource(), (S) sink);
        } else {
            pipe.reflux((T) payload.getSource(), (S) sink);
        }
    }

    private <T, S> Pipe<T, S> createPipeInstance() {
        try {
            return payload.getPipeClazz().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public IdRef<SinkMarker> createSinkMarkerId() {
        IdRef<?> sourceId = payload.getSourceId();
        IdRef<?> sinkId = payload.getSinkId();

        IdRef<SinkMarker> sinkMarkerId;
        if (sourceId.getId() != null) {
            sinkMarkerId = sourceId.createChildId(SinkMarker.class, sourceId.getId());
        } else {
            sinkMarkerId = sourceId.createChildId(SinkMarker.class, sourceId.getName());
        }

        sinkMarkerId.setParentId(sinkId);

        return sinkMarkerId;
    }

    public SourceMarker getSourceMarker() {
        return payload.getSourceMarker();
    }

    public Long getSourceVersion() {
        return payload.getSourceMarker().getVersion();
    }
}
