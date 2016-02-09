package io.yawp.repository.tools;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.Feature;
import io.yawp.repository.pipes.Pipe;

public class ReloadPipe extends Feature {

    public void now(String pipeClazzName) {
        Class<? extends Pipe> pipeClazz = getPipeClazz(pipeClazzName);
        yawp.driver().pipes().reload(pipeClazz);
    }

    private Class<? extends Pipe> getPipeClazz(String pipeClazzName) {
        return (Class<? extends Pipe>) ReflectionUtils.clazzForName(pipeClazzName);
    }

}
