package io.yawp.commons.utils.json;

import com.google.gson.*;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.lang.reflect.Type;

public class IdRefJsonSerializerDeserializer implements JsonSerializer<IdRef<?>>, JsonDeserializer<IdRef<?>> {

    private Repository r;

    public IdRefJsonSerializerDeserializer(Repository r) {
        this.r = r;
    }

    @Override
    public JsonElement serialize(IdRef<?> idRef, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(idRef.toString());
    }

    @Override
    public IdRef<?> deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        String path = json.getAsJsonPrimitive().getAsString();
        return IdRef.parse(r, null, path);
    }

}
