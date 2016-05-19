package io.yawp.repository;

import io.yawp.commons.utils.ReflectionUtils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LazyJsonAdapter implements JsonSerializer<LazyJson<?>>, JsonDeserializer<LazyJson<?>> {

	@Override
	public LazyJson<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Class<? extends Class> class1 = ReflectionUtils.getListGenericType(typeOfT).getClass();
		if (LazyJson.class.isAssignableFrom(class1)) {
			return LazyJson.parse(json.getAsJsonObject().toString(), ReflectionUtils.getListGenericType(typeOfT).getClass());
		} else {
			try {
				Type[] fieldValue = (Type[]) ReflectionUtils.getFieldValue(typeOfT, "typeArguments");
				String nameClass = (String) ReflectionUtils.getFieldValue(fieldValue[0], "name");
				return LazyJson.parse(json.getAsJsonObject().toString(), Class.forName(nameClass));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return LazyJson.parse(json.getAsJsonObject().toString(), null);
			}
		}
	}

	@Override
	public JsonElement serialize(LazyJson<?> src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
