package io.yawp.repository.actions;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.Feature;

public class Action<T> extends Feature {

    public <T> T from(String json, Class<T> clazz) {
        return JsonUtils.from(yawp, json, clazz);
    }

    public String to(Object object) {
        return JsonUtils.to(object);
    }

}
