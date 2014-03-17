package endpoint.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

	public static <T> T from(String json, Class<T> clazz) {
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		return from(jsonObject, clazz);
	}
	
	private static Gson buildGson() {
		Gson gson = new GsonBuilder().setDateFormat(DateUtils.TIMESTAMP_FORMAT).create();		
		return gson;
	}

	public static String to(Object o) {
		Gson gson = buildGson();
		return gson.toJson(o);
	}

	private static <T> T from(JsonObject json, Class<T> clazz) {
		Gson gson = buildGson();
		return gson.fromJson(json, clazz);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> fromArray(String json, Class<T> clazz) {
		List<T> result = new ArrayList<T>();

		Gson gson = buildGson();
		List<Map> listMap = gson.fromJson(json, List.class);
		for (Map map : listMap) {
			result.add(from(to(map), clazz));
		}

		return result;
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
