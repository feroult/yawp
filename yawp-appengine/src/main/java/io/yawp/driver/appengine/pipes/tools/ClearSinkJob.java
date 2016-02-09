package io.yawp.driver.appengine.pipes.tools;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SinkMarker;

import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class ClearSinkJob extends Job2<Void, Class<? extends Pipe>, String> {

    private transient Repository r;

    private transient Class<? extends Pipe> pipeClazz;

    private transient Class<?> sourceClazz;

    private transient IdRef<?> sinkId;

    @Override
    public Value<Void> run(Class<? extends Pipe> pipeClazz, String sinkUri) throws Exception {
        init(pipeClazz, sinkUri);
        return execute();
    }

    private void init(Class<? extends Pipe> pipeClazz, String sinkUri) {
        this.r = yawp();
        this.pipeClazz = pipeClazz;
        this.sourceClazz = ReflectionUtils.getFeatureEndpointClazz(pipeClazz);
        this.sinkId = IdRef.parse(r, sinkUri);
    }

    private Value<Void> execute() {
        System.out.println("clear sink job: " + sinkId);
        destroySinkMarkers();
        initSink();
        return null;
    }

    private void initSink() {
        Pipe pipe = createPipeInstance();
        Object sink = sinkId.fetch();
        pipe.clear(sink);
        r.save(sink);
    }

    private Pipe createPipeInstance() {
        try {
            return pipeClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void destroySinkMarkers() {
        List<IdRef<SinkMarker>> ids = r.query(SinkMarker.class).from(sinkId).ids();
        for (IdRef<SinkMarker> id : ids) {
            if (!id.getParentClazz().equals(sourceClazz)) {
                continue;
            }
            r.destroy(id);
        }
    }
}
