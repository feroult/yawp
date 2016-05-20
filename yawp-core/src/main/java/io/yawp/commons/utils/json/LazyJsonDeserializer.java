package io.yawp.commons.utils.json;

import com.google.gson.*;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.LazyJson;

import java.lang.reflect.Type;

public class LazyJsonDeserializer implements JsonDeserializer<LazyJson<?>> {

    @Override
    public LazyJson<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        Class<? extends Class> clazz = (Class<? extends Class>) ReflectionUtils.getGenericTypeArgumentAt(type, 0);
        return LazyJson.create(clazz, json.getAsJsonObject().toString());
    }

}
