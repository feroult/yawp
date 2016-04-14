package io.yawp.driver.appengine.pipes.flow;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;

import java.util.logging.Logger;

import static io.yawp.repository.Yawp.yawp;

@Endpoint(kind = "__yawp_pipe_works")
public class Work {

    private final static Logger logger = Logger.getLogger(Work.class.getName());

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
        log();

        Pipe<T, S> pipe = newPipeInstance();

        if (sinkMarker.isPresent()) {
            pipe.reflux((T) sinkMarker.getSource(), (S) sink);
        }

        if (payload.isPresent()) {
            pipe.flux((T) payload.getSource(), (S) sink);
            rememberSourceInSinkMarker(sinkMarker);
        }

        sinkMarker.setPresent(payload.isPresent());
        sinkMarker.setVersion(payload.getSourceMarker().getVersion());
    }

    private void log() {
        logger.info(String.format("join-work - pipe: %s, sourceId: %s", payload.getPipeClazz().getName(), payload.getSourceId().getUri()));
    }

    private void rememberSourceInSinkMarker(SinkMarker sinkMarker) {
        sinkMarker.setSourceJson(ReflectionUtils.getFeatureEndpointClazz(payload.getPipeClazz()), payload.getSourceJson());
    }

    private <T, S> Pipe<T, S> newPipeInstance() {
        return Pipe.newInstance(yawp(), payload.getPipeClazz());
    }

    public IdRef<SinkMarker> createSinkMarkerId() {
        IdRef<?> sourceId = payload.getSourceId();
        IdRef<?> sinkId = payload.getSinkId();

        IdRef<SinkMarker> sinkMarkerId;
        if (sourceId.getId() != null) {
            sinkMarkerId = IdRef.create(yawp(), SinkMarker.class, sourceId.getId());
            sinkMarkerId.setParentId(sinkId.createChildId(sourceId.getClazz(), sourceId.getId()));
        } else {
            sinkMarkerId = IdRef.create(yawp(), SinkMarker.class, sourceId.getName());
            sinkMarkerId.setParentId(sinkId.createChildId(sourceId.getClazz(), sourceId.getName()));
        }

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

    public IdRef<?> getSinkId() {
        return payload.getSinkId();
    }
}
