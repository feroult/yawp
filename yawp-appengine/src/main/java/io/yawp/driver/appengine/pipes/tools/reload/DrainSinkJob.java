package io.yawp.driver.appengine.pipes.tools.reload;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class DrainSinkJob extends Job3<Void, Class<? extends Pipe>, String, String> {

    private static final int BATCH_SIZE = 100;

    private transient Repository r;

    private transient Class<? extends Pipe> pipeClazz;

    private transient Class<?> sourceClazz;

    private transient IdRef<?> sinkId;

    private transient String cursor;

    @Override
    public Value<Void> run(Class<? extends Pipe> pipeClazz, String sinkUri, String cursor) throws Exception {
        init(pipeClazz, sinkUri, cursor);
        return execute();
    }

    private void init(Class<? extends Pipe> pipeClazz, String sinkUri, String cursor) {
        this.r = yawp();
        this.pipeClazz = pipeClazz;
        this.sourceClazz = ReflectionUtils.getFeatureEndpointClazz(pipeClazz);
        this.sinkId = IdRef.parse(r, sinkUri);
        this.cursor = cursor;
    }

    private Value<Void> execute() {
        List<? extends IdRef<?>> markerIds = sinkMarkerIds();

        FutureValue<Void> waitForClearSink = null;
        if (cursor != null) {
            waitForClearSink = futureCall(new DrainSinkJob(), immediate(pipeClazz), immediate(sinkId.getUri()), immediate(cursor));
        } else {
            clearSink();
        }

        destroySinkMarkers(markerIds);

        return waitForClearSink;
    }

    private void destroySinkMarkers(List<? extends IdRef<?>> markerIds) {
        for (IdRef<?> id : markerIds) {
            if (!id.getParentId().getClazz().equals(sourceClazz)) {
                continue;
            }
            r.destroy(id);
        }
    }

    private void clearSink() {
        Pipe pipe = createPipeInstance();
        Object sink = sinkId.fetch();
        pipe.drain(sink);
        r.save(sink);
    }

    private Pipe createPipeInstance() {
        try {
            return pipeClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<? extends IdRef<?>> sinkMarkerIds() {
        QueryBuilder<?> q = r.query(SinkMarker.class).from(sinkId).order("id").limit(BATCH_SIZE);
        if (cursor != null) {
            q.cursor(cursor);
        }
        List<? extends IdRef<?>> ids = q.ids();
        if (ids.size() < BATCH_SIZE) {
            cursor = null;
        } else {
            cursor = q.getCursor();
        }
        return ids;
    }
}
