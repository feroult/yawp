package io.yawp.commons.utils.json;

import com.google.gson.*;
import io.yawp.repository.IdRef;

import java.lang.reflect.Type;

import static io.yawp.repository.Yawp.yawp;

public class IdRefJsonSerializerDeserializer implements JsonSerializer<IdRef<?>>, JsonDeserializer<IdRef<?>> {

    @Override
    public JsonElement serialize(IdRef<?> idRef, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(idRef.toString());
    }

    @Override
    public IdRef<?> deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        String path = json.getAsJsonPrimitive().getAsString();
        return IdRef.parse(yawp(), null, path);
    }

}
