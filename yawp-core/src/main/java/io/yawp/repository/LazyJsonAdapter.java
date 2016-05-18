package io.yawp.repository;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LazyJsonAdapter implements JsonSerializer<LazyJson>, JsonDeserializer<LazyJson> {

	@Override
	public LazyJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return LazyJson.parse(json.getAsJsonObject());
	}

	@Override
	public JsonElement serialize(LazyJson src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
