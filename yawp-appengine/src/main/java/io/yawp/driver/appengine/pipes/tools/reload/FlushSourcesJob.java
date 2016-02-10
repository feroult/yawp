package io.yawp.driver.appengine.pipes.tools.reload;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;

import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class FlushSourcesJob extends Job1<Void, Class<? extends Pipe>> {

    private transient Class<? extends Pipe> pipeClazz;

    private transient Class<?> sourceClazz;

    @Override
    public Value<Void> run(Class<? extends Pipe> pipeClazz) throws Exception {
        init(pipeClazz);
        return execute();
    }

    private void init(Class<? extends Pipe> pipeClazz) {
        this.pipeClazz = pipeClazz;
        this.sourceClazz = ReflectionUtils.getFeatureEndpointClazz(pipeClazz);
    }

    private Value<Void> execute() {
        // TODO: do by chunks
        List<? extends IdRef<?>> ids = yawp(sourceClazz).ids();

        for (IdRef<?> id : ids) {
            futureCall(new FlushSourceJob(), immediate(pipeClazz), immediate(id.getUri()));
        }

        return null;
    }
}
