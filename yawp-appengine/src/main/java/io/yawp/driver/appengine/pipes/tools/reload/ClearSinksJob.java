package io.yawp.driver.appengine.pipes.tools.reload;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.query.QueryBuilder;

import java.util.LinkedList;
import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class ClearSinksJob extends Job2<Void, Class<? extends Pipe>, String> {

    private static final int BATCH_SIZE = 10;

    private transient Class<? extends Pipe> pipeClazz;

    private transient Class<?> sinkClazz;

    private transient String cursor;

    @Override
    public Value<Void> run(Class<? extends Pipe> pipeClazz, String cursor) throws Exception {
        init(pipeClazz, cursor);
        return execute();
    }

    private void init(Class<? extends Pipe> pipeClazz, String cursor) {
        this.pipeClazz = pipeClazz;
        this.sinkClazz = ReflectionUtils.getFeatureTypeArgumentAt(pipeClazz, 1);
        this.cursor = cursor;
    }

    private Value<Void> execute() {
        List<FutureValue<Void>> jobs = new LinkedList<>();

        List<? extends IdRef<?>> ids = sinkIds();

        if (cursor != null) {
            jobs.add(futureCall(new ClearSinksJob(), immediate(pipeClazz), immediate(cursor)));
        }

        for (IdRef<?> id : ids) {
            jobs.add(futureCall(new ClearSinkJob(), immediate(pipeClazz), immediate(id.getUri()), null));
        }

        waitFor(futureList(jobs));
        return null;
    }

    private List<? extends IdRef<?>> sinkIds() {
        QueryBuilder<?> q = yawp(sinkClazz).order("id").limit(BATCH_SIZE);
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
