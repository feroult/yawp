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

        String json = value.getJson();

        // This is done to avoid json serialize/deserialize to JsonObject since we already have the json as a String.
        // Could not find any better GSON API. JsonWriter wont let me write strings without escaping then.
        CustomJsonWriter customWriter = (CustomJsonWriter) out;

        begin(customWriter, value);
        customWriter.write(json.substring(1, json.length() - 1)); // Remove { and  }
        end(customWriter, value);
    }

    private void begin(CustomJsonWriter customWriter, LazyJson value) throws IOException {
        if (value.isJsonArray()) {
            customWriter.beginArray();
        } else {
            customWriter.beginObject();
        }
    }

    private void end(CustomJsonWriter customWriter, LazyJson value) throws IOException {
        if (value.isJsonArray()) {
            customWriter.endArray();
        } else {
            customWriter.endObject();
        }
    }


}
