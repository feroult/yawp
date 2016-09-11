package io.yawp.commons.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.yawp.repository.Repository;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class JsonUtilsBase {

    protected JsonUtilsBase() {
    }

    public abstract Object from(Repository r, String json, Type type);

    public abstract String to(Object o);

    public <T> T from(Repository r, String json, Class<T> clazz) {
        return (T) from(r, json, (Type) clazz);
    }

    public <T> List<T> fromList(Repository r, String json, Class<T> clazz) {
        return (List<T>) fromListRaw(r, json, clazz);
    }

    @SuppressWarnings("unchecked")
    public List<?> fromListRaw(Repository r, String json, Type valueType) {
        ParameterizedTypeImpl type = new ParameterizedTypeImpl(List.class, new Type[]{valueType}, null);
        return (List<?>) from(r, json, type);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> fromMap(Repository r, String json, Class<K> keyClazz, Class<V> valueClazz) {
        return (Map<K, V>) fromMapRaw(r, json, keyClazz, valueClazz);
    }

    public Map<?, ?> fromMapRaw(Repository r, String json, Type keyType, Type valueType) {
        ParameterizedTypeImpl type = new ParameterizedTypeImpl(Map.class, new Type[]{keyType, valueType}, null);
        return (Map<?, ?>) from(r, json, type);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, List<V>> fromMapList(Repository r, String json, Class<K> keyClazz, Class<V> valueClazz) {
        Type listType = new ParameterizedTypeImpl(List.class, new Type[]{valueClazz}, null);
        Type type = new ParameterizedTypeImpl(Map.class, new Type[]{keyClazz, listType}, null);
        return (Map<K, List<V>>) from(r, json, type);
    }

    public String readJson(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    public boolean isJsonArray(String json) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        return json.charAt(0) == '[';
    }

    // TODO: remove gson support here
    public List<String> getProperties(String json) {
        List<String> properties = new ArrayList<>();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        for (Map.Entry<String, JsonElement> property : jsonObject.entrySet()) {
            properties.add(property.getKey());
        }
        return properties;
    }

}
