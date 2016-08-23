package io.yawp.commons.utils.json2;

import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.ext.GensonBundle;
import com.owlike.genson.reflect.BeanProperty;
import com.owlike.genson.reflect.VisibilityFilter;
import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;

public class BaseGensonBundle extends GensonBundle {

    @Override
    public void configure(GensonBuilder builder) {
//        builder.useRuntimeType(true);
        builder.setSkipNull(true);
        configureDateFormat(builder);
        configureSchema(builder);
        configureConverters(builder);
    }

    private void configureDateFormat(GensonBuilder builder) {
        builder.useDateFormat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
    }

    private void configureSchema(GensonBuilder builder) {
        builder.useMethods(false);
        builder.useFields(true, new VisibilityFilter(Modifier.TRANSIENT, Modifier.STATIC));
    }

    private void configureConverters(GensonBuilder builder) {
        IdRefConverters.configure(builder);
        LazyJsonConverters.configure(builder);
        builder.withContextualFactory(new CustomContextualFactory());
    }

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


}