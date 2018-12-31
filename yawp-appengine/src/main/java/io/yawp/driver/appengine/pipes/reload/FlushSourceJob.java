package io.yawp.driver.appengine.pipes.reload;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;

import static io.yawp.repository.Yawp.yawp;

public class FlushSourceJob extends Job2<Void, Class<? extends Pipe>, String> {

    private transient Repository r;

    private transient Class<? extends Pipe> pipeClazz;

    private transient IdRef<?> sourceId;

    @Override
    public Value<Void> run(Class<? extends Pipe> pipeClazz, String sourceUri) throws Exception {
        init(pipeClazz, sourceUri);
        return execute();
    }

    private void init(Class<? extends Pipe> pipeClazz, String sourceUri) {
        this.r = yawp();
        this.pipeClazz = pipeClazz;
        this.sourceId = IdRef.parse(r, sourceUri);
    }

    private Value<Void> execute() {
        flushSource();
        return null;
    }

    private void flushSource() {
        try {
            r.begin();
            Pipe pipe = newPipeInstance();
            Object source = sourceId.fetch();
            r.driver().pipes().flux(pipe, source);
            r.commit();
        } finally {
            if (r.isTransactionInProgress()) {
                r.rollback();
            }
        }
    }

    private Pipe newPipeInstance() {
        return Pipe.newInstance(r, pipeClazz);
    }

}
