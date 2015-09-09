package io.yawp.commons.utils;

import io.yawp.commons.http.HttpVerb;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

public class EntityUtils {

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	public static void toEntity(Object object, Entity entity) {
		ObjectHolder objectH = new ObjectHolder(object);

		List<FieldModel> fieldModels = objectH.getModel().getFieldModels();

		for (FieldModel fieldModel : fieldModels) {
			if (fieldModel.isId()) {
				continue;
			}

			setEntityProperty(object, entity, fieldModel);
		}
	}

	public static <T> T toObject(Repository r, Entity entity, Class<T> clazz) {
		T object = createObjectInstance(clazz);

		ObjectHolder objectH = new ObjectHolder(object);
		objectH.setId(IdRef.fromKey(r, entity.getKey()));

		List<FieldModel> fieldModels = objectH.getModel().getFieldModels();

		for (FieldModel fieldModel : fieldModels) {
			if (fieldModel.isId()) {
				continue;
			}

			safeSetObjectProperty(r, entity, object, fieldModel);
		}

		return object;
	}

	private static <T> T createObjectInstance(Class<T> clazz) {
		try {

			Constructor<T> defaultConstructor = clazz.getDeclaredConstructor(new Class<?>[] {});
			defaultConstructor.setAccessible(true);
			return defaultConstructor.newInstance();

		} catch (InvocationTargetException e) {
			throw new RuntimeException("An exception was thrown when calling the default constructor of the class " + clazz.getSimpleName()
					+ ": ", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("The class " + clazz.getSimpleName()
					+ " must have a default constructor and cannot be an non-static inner class.", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("The class " + clazz.getSimpleName() + " must cannot be abstract.", e);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			throw new RuntimeException("Unexpected error: ", e);
		}
	}

	public static <T> String getActualFieldName(String fieldName, Class<T> clazz) {
		Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);
		FieldModel fieldModel = new FieldModel(field);

		if (fieldModel.isId()) {
			return Entity.KEY_RESERVED_PROPERTY;
		}

		if (fieldModel.isIndexNormalizable()) {
			return NORMALIZED_FIELD_PREFIX + fieldName;
		}

		return fieldName;
	}

	public static <T> Object getActualFieldValue(String fieldName, Class<T> clazz, Object value) {
		Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);
		FieldModel fieldModel = new FieldModel(field);

		if (fieldModel.isCollection(value)) {
			return getActualListFieldValue(fieldName, clazz, (Collection<?>) value);
		}

		if (fieldModel.isId()) {
			return getActualKeyFieldValue(clazz, value);
		}

		if (fieldModel.isEnum(value)) {
			return value.toString();
		}

		if (fieldModel.isIndexNormalizable()) {
			return normalizeValue(value);
		}

		if (value instanceof IdRef) {
			return ((IdRef<?>) value).getUri();
		}

		if (fieldModel.isDate() && value instanceof String) {
			return DateUtils.toTimestamp((String) value);
		}

		return value;
	}

	private static <T> Object getActualListFieldValue(String fieldName, Class<T> clazz, Collection<?> value) {
		Collection<?> objects = (Collection<?>) value;
		List<Object> values = new ArrayList<>();
		for (Object obj : objects) {
			values.add(getActualFieldValue(fieldName, clazz, obj));
		}
		return values;
	}

	private static <T> Key getActualKeyFieldValue(Class<T> clazz, Object value) {
		IdRef<?> idRef = (IdRef<?>) value;
		return idRef.asKey();
	}

	private static void setEntityProperty(Object object, Entity entity, FieldModel fieldModel) {
		Object value = getFieldValue(fieldModel, object);

		if (!fieldModel.hasIndex()) {
			entity.setUnindexedProperty(fieldModel.getName(), value);
			return;
		}

		if (fieldModel.isIndexNormalizable()) {
			entity.setProperty(NORMALIZED_FIELD_PREFIX + fieldModel.getName(), normalizeValue(value));
			entity.setUnindexedProperty(fieldModel.getName(), value);
			return;
		}

		entity.setProperty(fieldModel.getName(), value);
	}

	private static Object normalizeValue(Object o) {
		if (o == null) {
			return null;
		}

		if (!o.getClass().equals(String.class)) {
			return o;
		}

		return StringUtils.stripAccents((String) o).toLowerCase();
	}

	private static Object getFieldValue(FieldModel fieldModel, Object object) {
		Object value = fieldModel.getValue(object);

		if (value == null) {
			return null;
		}

		if (fieldModel.isEnum(value)) {
			return value.toString();
		}

		if (fieldModel.isSaveAsJson()) {
			return new Text(JsonUtils.to(value));
		}

		if (fieldModel.isIdRef()) {
			IdRef<?> idRef = (IdRef<?>) value;
			return idRef.getUri();
		}

		if (fieldModel.isSaveAsText()) {
			return new Text(value.toString());
		}

		return value;
	}

	private static <T> void safeSetObjectProperty(Repository r, Entity entity, T object, FieldModel fieldModel) {
		try {
			setObjectProperty(r, object, entity, fieldModel, fieldModel.getField());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> void setObjectProperty(Repository r, T object, Entity entity, FieldModel fieldModel, Field field)
			throws IllegalAccessException {
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

		if (fieldModel.isInt()) {
			setIntProperty(object, field, value);
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

		field.set(object, value);
	}

	public static int listSize(Object value) {
		if (value == null) {
			return 0;
		}
		if (value.getClass().isArray()) {
			return Array.getLength(value);
		}
		if (Collection.class.isAssignableFrom(value.getClass())) {
			return Collection.class.cast(value).size();
		}
		if (Iterable.class.isAssignableFrom(value.getClass())) {
			return iterableSize(value);
		}
		throw new RuntimeException("Value used with operator 'in' is not an array or list.");
	}

	private static int iterableSize(Object value) {
		Iterator<?> it = Iterable.class.cast(value).iterator();
		int i = 0;
		while (it.hasNext()) {
			it.next();
			i++;
		}
		return i;
	}

	private static <T> void setIdRefProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, IdRef.parse(r, HttpVerb.GET, (String) value));
	}

	private static <T> void setIntProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, ((Long) value).intValue());
	}

	private static <T> void setTextProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, ((Text) value).getValue());
	}

	private static <T> void setJsonProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
		String json = ((Text) value).getValue();
		field.set(object, JsonUtils.from(r, json, field.getGenericType()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> void setEnumProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, Enum.valueOf((Class) field.getType(), value.toString()));
	}
}
