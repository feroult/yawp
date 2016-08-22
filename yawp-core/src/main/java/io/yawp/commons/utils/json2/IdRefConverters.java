package io.yawp.commons.utils.json2;

import com.owlike.genson.*;
import com.owlike.genson.convert.DefaultConverters;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import io.yawp.repository.IdRef;

import java.lang.reflect.Type;
import java.util.*;

import static com.owlike.genson.reflect.TypeUtil.*;
import static io.yawp.repository.Yawp.yawp;

public class IdRefConverters {

    public static final IdRefConverter idRefConverter = new IdRefConverter();

    public static final IdRefConverterFactory idRfConverterFactory = new IdRefConverterFactory();

    public static final IdRefMapConverterFactory idRefMapConverterFactory = new IdRefMapConverterFactory();

    public static void configure(GensonBuilder builder) {
        builder.withConverterFactory(idRfConverterFactory);
        builder.withConverterFactory(idRefMapConverterFactory);
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
            return new IdRefConverter();
        }
    }

    public static class IdRefMapConverterFactory implements Factory<Converter<? extends Map<?, ?>>> {

        @Override
        public Converter<? extends Map<?, ?>> create(Type type, Genson genson) {
            Type expandedType = type;
            if (getRawClass(type).getTypeParameters().length == 0) {
                expandedType = expandType(lookupGenericType(Map.class, getRawClass(type)), type);
            }

            Type keyType = typeOf(0, expandedType);
            Class<?> keyRawClass = getRawClass(keyType);

            if (keyRawClass.equals(IdRef.class)) {
                Type valueType = typeOf(1, expandedType);
                return createConverter(getRawClass(type), idRefAdapter, genson.provideConverter(valueType));
            }

            return null;
        }

        public final DefaultConverters.KeyAdapter<IdRef> idRefAdapter = new DefaultConverters.KeyAdapter<IdRef>() {
            @Override
            public IdRef adapt(String str) {
                return IdRef.parse(yawp(), null, str);
            }

            @Override
            public String adapt(IdRef idRef) {
                return idRef.getUri();
            }
        };

        @SuppressWarnings("unchecked")
        private <K, V> DefaultConverters.MapConverter<K, V> createConverter(Class<?> typeOfMap,
                                                                            DefaultConverters.KeyAdapter<K> keyAdapter, Converter<V> valueConverter) {
            if (Properties.class.equals(typeOfMap))
                return new DefaultConverters.PropertiesConverter(keyAdapter, valueConverter);

            if (Hashtable.class.equals(typeOfMap))
                return new DefaultConverters.HashTableConverter<K, V>(keyAdapter, valueConverter);

            if (TreeMap.class.equals(typeOfMap))
                return new DefaultConverters.TreeMapConverter<K, V>(keyAdapter, valueConverter);

            if (LinkedHashMap.class.equals(typeOfMap))
                return new DefaultConverters.LinkedHashMapConverter<K, V>(keyAdapter, valueConverter);

            return new DefaultConverters.HashMapConverter<K, V>(keyAdapter, valueConverter);
        }
    }
}
