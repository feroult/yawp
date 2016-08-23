package io.yawp.commons.utils.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.yawp.commons.utils.DateUtils;
import io.yawp.commons.utils.json.JsonUtilsBase;
import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;
import io.yawp.repository.Repository;

import java.io.StringWriter;
import java.lang.reflect.Type;

public class GsonJsonUtils extends JsonUtilsBase {

    private Gson gson;

    public GsonJsonUtils() {
        super();
        this.gson = buildGson();
    }

    private Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(DateUtils.TIMESTAMP_FORMAT);
        builder.registerTypeAdapter(IdRef.class, new IdRefJsonSerializerDeserializer());
        builder.registerTypeAdapter(LazyJson.class, new LazyJsonDeserializer());
        builder.registerTypeAdapterFactory(new LazyJsonTypeAdapterFactory());
        return builder.create();
    }

    @Override
    public Object from(Repository r, String json, Type type) {
        JsonElement jsonElement = new JsonParser().parse(json);
        return gson.fromJson(jsonElement, type);
    }

    @Override
    public String to(Object o) {
        if (o == null) {
            return gson.toJson(o);
        }
        StringWriter out = new StringWriter();
        gson.toJson(o, o.getClass(), new CustomJsonWriter(out));
        return out.toString();
    }

}
