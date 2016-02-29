package io.yawp.tools.pipes;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.pipes.Pipe;
import io.yawp.tools.Tool;

public class ReloadPipe extends Tool {

    public static final String PIPE_PARAM = "pipe";

    @Override
    public void execute() {
        String pipeClazzName = params.get(PIPE_PARAM);

        if (pipeClazzName == null) {
            pw.println("use: pipes/reload?pipe=pipe-class-name");
            return;
        }
        reload(pipeClazzName);
        pw.println("ok");
    }

    private void reload(String pipeClazzName) {
        Class<? extends Pipe> pipeClazz = getPipeClazz(pipeClazzName);
        yawp.driver().pipes().reload(pipeClazz);
    }

    private Class<? extends Pipe> getPipeClazz(String pipeClazzName) {
        return (Class<? extends Pipe>) ReflectionUtils.clazzForName(pipeClazzName);
    }

}
