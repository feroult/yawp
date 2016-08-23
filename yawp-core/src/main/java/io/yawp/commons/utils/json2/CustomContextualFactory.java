package io.yawp.commons.utils.json2;

import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.reflect.BeanProperty;
import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;

public class CustomContextualFactory implements ContextualFactory {

    @Override
    public Converter create(BeanProperty property, Genson genson) {
        if (property.getRawClass().isAssignableFrom(IdRef.class)) {
            return IdRefConverters.idRefConverter;
        }
        if (property.getRawClass().isAssignableFrom(LazyJson.class)) {
            return LazyJsonConverters.get(property.getName(), property.getType());
        }
        return null;
    }
}