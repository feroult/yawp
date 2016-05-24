package io.yawp.commons.utils.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.yawp.repository.LazyJson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class LazyJsonDeserializer implements JsonDeserializer<LazyJson<?>> {

    @Override
    public LazyJson<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        Type rootType = ((ParameterizedType) type).getActualTypeArguments()[0];

        if (isJsonArray(rootType)) {
            return createList(rootType, json);
        }

        return createObject(rootType, json);
    }

    private boolean isJsonArray(Type rootType) {
        return rootType instanceof ParameterizedType && List.class.isAssignableFrom((Class<?>) ((ParameterizedType) rootType).getRawType());
    }

    private LazyJson<?> createObject(Type rootType, JsonElement json) {
        return LazyJson.$create(rootType, json.getAsJsonObject().toString());
    }

    private LazyJson<?> createList(Type rootType, JsonElement json) {
        return LazyJson.$create(rootType, json.getAsJsonArray().toString());
    }

}
