package io.yawp.driver.appengine.pipes;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;
import io.yawp.repository.models.FieldModel;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.models.ObjectModel;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;

import static io.yawp.repository.Yawp.yawp;

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

    public <T, S> void execute(Object sink, SinkMarker sinkMarker, String indexHash) {
        Pipe<T, S> pipe = createPipeInstance();

        logWork(sink, sinkMarker, indexHash);

        if (payload.isPresent()) {
            if (sinkMarker.isPresent()) {
                pipe.reflux((T) sinkMarker.getSource(), (S) sink);
            }
            pipe.flux((T) payload.getSource(), (S) sink);
            rememberSourceInSinkMarker(sinkMarker);
        } else {
            pipe.reflux((T) payload.getSource(), (S) sink);
        }

        sinkMarker.setVersion(payload.getSourceMarker().getVersion());
        sinkMarker.setPresent(payload.isPresent());
    }

    private void logWork(Object sink, SinkMarker sinkMarker, String indexHash) {
        Class<?> endpointClazz = ReflectionUtils.getFeatureEndpointClazz(payload.getPipeClazz());
        ObjectHolder holder = new ObjectHolder(payload.getSource());
        ObjectModel model = new ObjectModel(endpointClazz);
        FieldModel group = model.getFieldModel("group");

        System.out.print(String.format("%s -> id: %s -- version: new=%d, old=%d -- ", indexHash, holder.getId(), payload.getSourceMarker().getVersion(), sinkMarker.getVersion()));

        if (payload.isPresent()) {
            if (sinkMarker.isPresent()) {
                System.out.print(String.format("reflux: %s -- ", group.getValue(sinkMarker.getSource())));
            }
            System.out.println(String.format("flux: %s", group.getValue(payload.getSource())));

        } else {
            System.out.println(String.format("reflux: %s", group.getValue(payload.getSource())));
        }
    }

    private void rememberSourceInSinkMarker(SinkMarker sinkMarker) {
        sinkMarker.setSourceJson(ReflectionUtils.getFeatureEndpointClazz(payload.getPipeClazz()), payload.getSourceJson());
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
}
