package io.yawp.commons.utils.json.genson;

import com.owlike.genson.*;
import com.owlike.genson.stream.ObjectWriter;
import io.yawp.commons.utils.json.JsonUtilsBase;
import io.yawp.repository.Repository;

import java.io.StringWriter;
import java.lang.reflect.Type;

public class GensonJsonUtils extends JsonUtilsBase {

    private Genson genson;

    public GensonJsonUtils() {
        super();
        this.genson = new GensonBuilder().withBundle(new BaseGensonBundle()).create();
    }

    @Override
    public Object from(Repository r, String json, Type type) {
        return genson.deserialize(json, GenericType.of(type));
    }

    @Override
    public String to(Object o) {
        StringWriter sw = new StringWriter();
        ObjectWriter writer = createWriter(sw);

        if (o == null) {
            try {
                writer.writeNull();
                writer.flush();
            } catch (Exception e) {
                throw new JsonBindingException("Could not serialize null value.", e);
            }
        } else {
            genson.serialize(o, o.getClass(), writer, new Context(genson));
        }

        return sw.toString();
    }

    private ObjectWriter createWriter(StringWriter sw) {
        return new RawJsonWriter(sw, genson.isSkipNull(), genson.isHtmlSafe(), false);
    }

}
