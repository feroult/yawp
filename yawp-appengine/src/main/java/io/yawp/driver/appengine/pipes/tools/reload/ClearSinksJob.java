package io.yawp.driver.appengine.pipes.tools.reload;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;

import java.util.LinkedList;
import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class ClearSinksJob extends Job1<Void, Class<? extends Pipe>> {

    private transient Class<? extends Pipe> pipeClazz;

    private transient Class<?> sinkClazz;

    @Override
    public Value<Void> run(Class<? extends Pipe> pipeClazz) throws Exception {
        init(pipeClazz);
        return execute();
    }

    private void init(Class<? extends Pipe> pipeClazz) {
        this.pipeClazz = pipeClazz;
        this.sinkClazz = ReflectionUtils.getFeatureTypeArgumentAt(pipeClazz, 1);
    }

    private Value<Void> execute() {
        List<FutureValue<Void>> sinkJobs = new LinkedList<>();

        for (IdRef<?> id : sinkIds()) {
            sinkJobs.add(futureCall(new ClearSinkJob(), immediate(pipeClazz), immediate(id.getUri())));
        }

        return null;
    }

    private List<? extends IdRef<?>> sinkIds() {
        return yawp(sinkClazz).ids();
    }
}
