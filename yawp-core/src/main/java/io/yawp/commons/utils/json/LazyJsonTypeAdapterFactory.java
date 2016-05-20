package io.yawp.commons.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import io.yawp.repository.LazyJson;

import java.io.IOException;

public class LazyJsonTypeAdapterFactory extends CustomTypeAdapterFactory<LazyJson> {
    public LazyJsonTypeAdapterFactory() {
        super(LazyJson.class);
    }

    @Override
    protected void write(JsonWriter out, LazyJson value, TypeAdapter<JsonElement> elementAdapter, TypeAdapter<LazyJson> delegate) throws IOException {
        if (value == null || value.getJson() == null) {
            out.nullValue();
            return;
        }

        // This is done to avoid json parsing to JsonObject, since we already have the json as a String.
        // Could not find any better GSON API. JsonWriter wont let me write string without scaping then.
        CustomJsonWriter customWriter = (CustomJsonWriter) out;
        out.beginObject();
        customWriter.write(value.getJson().substring(1));
    }

}
