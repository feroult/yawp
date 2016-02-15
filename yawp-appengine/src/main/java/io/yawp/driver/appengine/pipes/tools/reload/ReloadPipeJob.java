package io.yawp.driver.appengine.pipes.tools.reload;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;
import io.yawp.repository.pipes.Pipe;

public class ReloadPipeJob extends Job1<Void, Class<? extends Pipe>> {

    private transient Class<? extends Pipe> pipeClazz;

    private transient Class<?> sinkClazz;

    @Override
    public Value<Void> run(Class<? extends Pipe> pipeClazz) throws Exception {
        init(pipeClazz);
        return execute();
    }

    private void init(Class<? extends Pipe> pipeClazz) {
        this.pipeClazz = pipeClazz;
    }

    private Value<Void> execute() {
        JobSetting.WaitForSetting waitClearSinks = waitFor(futureCall(new ClearSinksJob(), immediate(pipeClazz), null));
        return futureCall(new FlushSourcesJob(), immediate(pipeClazz), null, waitClearSinks);
    }
}
