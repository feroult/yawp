package io.yawp.driver.appengine.pipes;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;

@Endpoint(kind = "__yawp_pipe_works")
public class Work {

    @Id
    private IdRef<Work> id;

    @Index(normalize = false)
    private String indexHash;

    @Json
    private Payload payload;

    public Work() {
    }

    public Work(String indexHash, Payload payload) {
        this.indexHash = indexHash;
        this.payload = payload;
    }

    public <T, S> void execute(Object sink, SinkMarker sinkMarker) {
        Pipe<T, S> pipe = createPipeInstance();

        if (payload.isPresent()) {
            if (sinkMarker.isPresent()) {
                pipe.reflux((T) sinkMarker.getSource(), (S) sink);
            }
            pipe.flux((T) payload.getSource(), (S) sink);
            sinkMarker.setSource(payload.getSource());
        } else {
            pipe.reflux((T) payload.getSource(), (S) sink);
        }


        sinkMarker.setVersion(payload.getSourceMarker().getVersion());
        sinkMarker.setPresent(payload.isPresent());
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

    public IdRef<Work> getId() {
        return id;
    }
}
