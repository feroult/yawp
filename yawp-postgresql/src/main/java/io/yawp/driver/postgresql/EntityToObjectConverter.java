package io.yawp.driver.postgresql;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.DateUtils;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;
import io.yawp.repository.Repository;
import io.yawp.repository.models.FieldModel;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.models.ObjectModel;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EntityToObjectConverter {

    private Repository r;

    public EntityToObjectConverter(Repository r) {
        this.r = r;
    }

    public Object convert(ObjectModel model, Entity entity) {
        Object object = model.createInstance();

        ObjectHolder objectHolder = new ObjectHolder(object);
        objectHolder.setId(IdRefToKey.toIdRef(r, entity.getKey(), model));

        List<FieldModel> fieldModels = objectHolder.getModel().getFieldModels();

        for (FieldModel fieldModel : fieldModels) {
            if (fieldModel.isId()) {
                continue;
            }

            if (fieldModel.isTransient()) {
                continue;
            }

            safeSetObjectProperty(entity, object, fieldModel);
        }

        return object;
    }

    private <T> void safeSetObjectProperty(Entity entity, T object, FieldModel fieldModel) {
        try {
            setObjectProperty(object, entity, fieldModel, fieldModel.getField());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void setObjectProperty(T object, Entity entity, FieldModel fieldModel, Field field) throws IllegalAccessException {
        Object value = entity.getProperty(field.getName());

        if (value == null) {
            field.set(object, null);
            return;
        }

        if (fieldModel.isEnum()) {
            setEnumProperty(object, field, value);
            return;
        }

        if (fieldModel.isSaveAsJson()) {
            setJsonProperty(r, object, field, value);
            return;
        }

        if (fieldModel.isSaveAsLazyJson()) {
            setLazyJsonProperty(object, field, value);
            return;
        }

        if (fieldModel.isInt()) {
            setIntProperty(object, field, value);
            return;
        }

        // overrides
        if (fieldModel.isLong()) {
            setLongProperty(object, field, value);
            return;
        }

        // overrides
        if (fieldModel.isDate()) {
            setDateProperty(object, field, value);
            return;
        }

        if (fieldModel.isIdRef()) {
            setIdRefProperty(r, object, field, value);
            return;
        }

        if (fieldModel.isSaveAsText()) {
            setTextProperty(object, field, value);
            return;
        }

        if (fieldModel.isListOfIds()) {
            setListOfIdsProperty(object, field, value);
        }

        field.set(object, value);
    }

    private <T> void setIdRefProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
        field.set(object, IdRef.parse(r, HttpVerb.GET, (String) value));
    }

    private <T> void setIntProperty(T object, Field field, Object value) throws IllegalAccessException {
        // override
        field.set(object, ((Number) value).intValue());
    }

    // override
    private <T> void setLongProperty(T object, Field field, Object value) throws IllegalAccessException {
        field.set(object, ((Number) value).longValue());
    }

    // override
    private <T> void setDateProperty(T object, Field field, Object value) throws IllegalAccessException {
        field.set(object, DateUtils.toTimestamp((String) value));
    }

    private <T> void setTextProperty(T object, Field field, Object value) throws IllegalAccessException {
        // override
        field.set(object, ((String) value));
    }

    private <T> void setJsonProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
        // override
        String json = (String) value;
        field.set(object, JsonUtils.from(r, json, field.getGenericType()));
    }

    private <T> void setLazyJsonProperty(T object, Field field, Object value) throws IllegalAccessException {
        String json = (String) value;
        Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        field.set(object, LazyJson.$create(type, json));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void setEnumProperty(T object, Field field, Object value) throws IllegalAccessException {
        field.set(object, Enum.valueOf((Class) field.getType(), value.toString()));
    }

    private <T> void setListOfIdsProperty(T object, Field field, Object value) throws IllegalAccessException {
        List<String> uris = (List<String>) value;
        List<IdRef<?>> ids = new ArrayList<>(uris.size());
        Class<?> listGenericClazz = ReflectionUtils.getListGenericType(field.getGenericType());

        for (String uri : uris) {
            ids.add(IdRef.parse(r, HttpVerb.GET, uri));
        }

        field.set(object, ids);
    }
}
