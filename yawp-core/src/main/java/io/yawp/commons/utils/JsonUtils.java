package io.yawp.commons.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.yawp.commons.utils.json.JsonUtilsBase;
import io.yawp.commons.utils.json.gson.GsonJsonUtils;
import io.yawp.repository.Repository;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JsonUtils {
    private static JsonUtilsBase jsonUtils;

    private JsonUtils() {
    }

    static {
        init();
    }

    private static void init() {
        jsonUtils = new GsonJsonUtils();
    }

    public static Object from(Repository r, String json, Type type) {
        return jsonUtils.from(r, json, type);
    }

    public static String to(Object o) {
        return jsonUtils.to(o);
    }

    public static <T> T from(Repository r, String json, Class<T> clazz) {
        return jsonUtils.from(r, json, clazz);
    }

    public static <T> List<T> fromList(Repository r, String json, Class<T> clazz) {
        return jsonUtils.fromList(r, json, clazz);
    }

    public static List<?> fromListRaw(Repository r, String json, Type valueType) {
        return jsonUtils.fromListRaw(r, json, valueType);
    }

    public static <K, V> Map<K, V> fromMap(Repository r, String json, Class<K> keyClazz, Class<V> valueClazz) {
        return jsonUtils.fromMap(r, json, keyClazz, valueClazz);
    }

    public static Map<?, ?> fromMapRaw(Repository r, String json, Type keyType, Type valueType) {
        return jsonUtils.fromMapRaw(r, json, keyType, valueType);
    }

    public static <K, V> Map<K, List<V>> fromMapList(Repository r, String json, Class<K> keyClazz, Class<V> valueClazz) {
        return jsonUtils.fromMapList(r, json, keyClazz, valueClazz);
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
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        return json.charAt(0) == '[';
    }

    public static List<String> getProperties(String json) {
        List<String> properties = new ArrayList<String>();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        for (Entry<String, JsonElement> property : jsonObject.entrySet()) {
            properties.add(property.getKey());
        }
        return properties;
    }

}
