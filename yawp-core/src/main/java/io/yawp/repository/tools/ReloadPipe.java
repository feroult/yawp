package io.yawp.repository.tools;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.Feature;

public class ReloadPipe extends Feature {

    public void now(String pipeClazzName) {
        Class<?> pipeClazz = ReflectionUtils.clazzForName(pipeClazzName);
        yawp.driver().pipes().reload(pipeClazz);
    }

}
