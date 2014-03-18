package endpoint.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import endpoint.DatastoreObject;
import endpoint.Index;

public class EntityUtils {

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	public static String getKind(Class<? extends DatastoreObject> clazz) {
		return clazz.getSimpleName();
	}

	public static void toEntity(DatastoreObject object, Entity entity) {
		Field[] fields = getFields(object.getClass());

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (isControlField(field)) {
				continue;
			}

			if (isListField(field)) {
				continue;
			}

			setEntityProperty(object, entity, field);
		}
	}

	public static <T extends DatastoreObject> T toObject(Entity entity, Class<T> clazz) {
		try {
			T object = clazz.newInstance();

			object.setKey(entity.getKey());

			Field[] fields = getFields(clazz);

			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if (isControlField(field)) {
					continue;
				}

				if (isListField(field)) {
					continue;
				}

				setObjectProperty(object, entity, field);
			}

			return object;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends DatastoreObject> Field[] getFields(Class<T> clazz) {
		return ArrayUtils.addAll(DatastoreObject.class.getDeclaredFields(), clazz.getDeclaredFields());
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends DatastoreObject> getListClass(Field field) {
		Type genericFieldType = field.getGenericType();
		if (genericFieldType instanceof ParameterizedType) {
			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			for (Type fieldArgType : fieldArgTypes) {
				return (Class<? extends DatastoreObject>) fieldArgType;
			}
		}

		throw new RuntimeException("cant find list generic type");
	}

	public static boolean isListField(Field field) {
		return List.class.isAssignableFrom(field.getType());
	}

	public static <T extends DatastoreObject> String getIndexFieldName(String fieldName, Class<T> clazz) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			Index index = field.getAnnotation(Index.class);

			if (index.normalize()) {
				return NORMALIZED_FIELD_PREFIX + fieldName;
			}

			return fieldName;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends DatastoreObject> Object getIndexFieldValue(String fieldName, Class<T> clazz, Object value) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			Index index = field.getAnnotation(Index.class);

			if (index.normalize()) {
				return normalizeValue(value);
			}

			return value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void setEntityProperty(DatastoreObject object, Entity entity, Field field) {
		Object value = getFieldValue(field, object);

		Index index = field.getAnnotation(Index.class);

		if (index == null) {
			entity.setUnindexedProperty(field.getName(), value);
			return;
		}

		if (index.normalize()) {
			entity.setProperty(NORMALIZED_FIELD_PREFIX + field.getName(), normalizeValue(value));
			entity.setUnindexedProperty(field.getName(), value);
			return;
		}

		entity.setProperty(field.getName(), value);
	}

	private static Object normalizeValue(Object o) {
		if (o == null) {
			return null;
		}

		if (!o.getClass().equals(String.class)) {
			return o;
		}

		return StringUtils.stripAccents((String) o);
	}

	private static Object getFieldValue(Field field, DatastoreObject object) {
		try {
			field.setAccessible(true);
			Object value = field.get(object);

			if (isEnum(value)) {
				return value.toString();
			}

			return value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isEnum(Object value) {
		return value != null && value.getClass().isEnum();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends DatastoreObject> void setObjectProperty(T object, Entity entity, Field field) throws IllegalAccessException {
		field.setAccessible(true);

		Object value = entity.getProperty(field.getName());

		if (field.getType().isEnum()) {
			if (value != null) {
				value = Enum.valueOf((Class) field.getType(), value.toString());
			}
			field.set(object, value);
		} else if (field.getType().getName().equals("int")) {
			field.set(object, ((Long) value).intValue());
		} else {
			field.set(object, value);
		}
	}

	private static boolean isControlField(Field field) {
		return Key.class.equals(field.getType()) || field.isSynthetic();
	}

}
