package io.yawp.driver.appengine.pipes.tools;

import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SinkMarker;

import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class ClearSinkTask implements DeferredTask {

    private Class<? extends Pipe> pipeClazz;

    private String sinkUri;

    private transient Repository r;

    private transient Class<?> sourceClazz;

    private transient IdRef<?> sinkId;

    public ClearSinkTask(Class<? extends Pipe> pipeClazz, String sinkUri) {
        this.r = yawp();
        this.pipeClazz = pipeClazz;
        this.sinkUri = sinkUri;
    }

    @Override
    public void run() {
        try {
            r.begin();
            init();
            execute();
            r.commit();
        } finally {
            if (r.isTransationInProgress()) {
                r.rollback();
            }
        }
    }

    private void init() {
        this.sourceClazz = ReflectionUtils.getFeatureEndpointClazz(pipeClazz);
        this.sinkId = IdRef.parse(yawp(), sinkUri);
    }

    private void execute() {
        destroySinkMarkers();
        initSink();
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
