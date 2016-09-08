package io.yawp.commons.utils.json.genson;

import com.owlike.genson.Converter;
import com.owlike.genson.Factory;
import com.owlike.genson.Genson;
import com.owlike.genson.convert.DefaultConverters;
import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.owlike.genson.reflect.TypeUtil.*;
import static io.yawp.repository.Yawp.yawp;

public class CustomMapConverterFactory implements Factory<Converter<? extends Map<?, ?>>> {

    public static final CustomMapConverterFactory instance = new CustomMapConverterFactory();

    @Override
    public Converter<? extends Map<?, ?>> create(Type type, Genson genson) {
        Type expandedType = type;
        if (getRawClass(type).getTypeParameters().length == 0) {
            expandedType = expandType(lookupGenericType(Map.class, getRawClass(type)), type);
        }

        Type keyType = typeOf(0, expandedType);
        Type valueType = typeOf(1, expandedType);
        Class<?> keyRawClass = getRawClass(keyType);

        DefaultConverters.KeyAdapter<?> keyAdapter;
        if (keyRawClass.equals(IdRef.class)) {
            keyAdapter = this.idRefAdapter;
        } else {
            keyAdapter = DefaultConverters.MapConverterFactory.keyAdapter(keyRawClass);
        }

        Converter<Object> valueConverter;
        if (valueIsLazyJsonIfList(valueType)) {
            valueConverter = LazyJsonConverters.get(null, valueType);
        } else {
            valueConverter = genson.provideConverter(valueType);
        }

        if (keyAdapter != null) {
            return createConverter(getRawClass(type), keyAdapter, valueConverter);
        }

        return null;
    }

    private boolean valueIsLazyJsonIfList(Type valueType) {
        if (!(valueType instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType pValueType = (ParameterizedType) valueType;
        if (!pValueType.getRawType().equals(LazyJson.class)) {
            return false;
        }
        Type elementType = pValueType.getActualTypeArguments()[0];
        if (!(elementType instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType pElementType = (ParameterizedType) elementType;
        return pElementType.getRawType().equals(List.class);
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