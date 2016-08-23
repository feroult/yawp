package io.yawp.commons.utils.json;

import com.owlike.genson.*;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import io.yawp.repository.IdRef;

import java.lang.reflect.Type;

import static io.yawp.repository.Yawp.yawp;

public class IdRefConverters {

    public static final IdRefConverter idRefConverter = new IdRefConverter();

    public static final IdRefConverterFactory idRfConverterFactory = new IdRefConverterFactory();

    public static void configure(GensonBuilder builder) {
        builder.withConverterFactory(idRfConverterFactory);
    }

    public static class IdRefConverter implements Converter<IdRef> {
        @Override
        public void serialize(IdRef idRef, ObjectWriter writer, Context ctx) throws Exception {
            writer.writeString(idRef.toString());
        }

        @Override
        public IdRef deserialize(ObjectReader reader, Context ctx) throws Exception {
            String json = reader.valueAsString();
            return IdRef.parse(yawp(), null, json);
        }
    }

    public static class IdRefConverterFactory implements Factory<Converter<IdRef>> {
        @Override
        public Converter<IdRef> create(Type type, Genson genson) {
            return idRefConverter;
        }
    }
}
