package io.yawp.commons.utils.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.yawp.repository.LazyJson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class LazyJsonDeserializer implements JsonDeserializer<LazyJson<?>> {

    @Override
    public LazyJson<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        Type rootType = ((ParameterizedType) type).getActualTypeArguments()[0];

        if (!(rootType instanceof ParameterizedType)) {
            return createObject(rootType, json);
        }

        ParameterizedType parameterizedType = (ParameterizedType) rootType;

        if (List.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
            return createList(parameterizedType, json);
        }

        if (Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
            return createMap(parameterizedType, json);
        }


        throw new RuntimeException("Invalid LazyJson Type: " + type);
    }

    private LazyJson<?> createObject(Type rootType, JsonElement json) {
        String jsonString = json.getAsJsonObject().toString();
        return LazyJson.$create((Class<?>) rootType, jsonString);
    }

    private LazyJson<?> createList(ParameterizedType parameterizedType, JsonElement json) {
        Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        return LazyJson.$createList(clazz, json.getAsJsonArray().toString());
    }

    private LazyJson<?> createMap(ParameterizedType parameterizedType, JsonElement json) {
        Class<?> keyClazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[1];
        return LazyJson.$createMap(keyClazz, clazz, json.getAsJsonObject().toString());
    }

}
