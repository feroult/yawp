package endpoint.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

	public static <T> T from(String json, Class<T> clazz) {
		JsonElement jsoneElement = (JsonElement) new JsonParser().parse(json);
		Gson gson = buildGson();
		return gson.fromJson(jsoneElement, clazz);
	}

	private static Gson buildGson() {
		Gson gson = new GsonBuilder().setDateFormat(DateUtils.TIMESTAMP_FORMAT).create();
		return gson;
	}

	public static String to(Object o) {
		Gson gson = buildGson();
		return gson.toJson(o);
	}

	@SuppressWarnings({ "unchecked" })
	public static <T> List<T> fromArray(String json, Class<T> clazz) {
		List<T> result = new ArrayList<T>();

		Gson gson = buildGson();
		List<Object> list = gson.fromJson(json, List.class);
		for (Object obj : list) {
			result.add(from(to(obj), clazz));
		}

		return result;
	}

	public static Map<Long, String> fromMap(String json, Class<?> clazz) {
		Type type = getMapTypeToken(clazz);
		JsonElement jsoneElement = (JsonElement) new JsonParser().parse(json);
		Gson gson = buildGson();
		return gson.fromJson(jsoneElement, type);
	}

	private static Type getMapTypeToken(Class<?> clazz) {
		return new TypeToken<Map<Long, ?>>() {
		}.getType();
	}

	public static String readJson(BufferedReader reader) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return sb.toString();
	}

	public static boolean isJsonArray(String json) {
		JsonElement parsed = new JsonParser().parse(json);
		return parsed.isJsonArray();
	}
}
