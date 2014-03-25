package endpoint.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import endpoint.DatastoreObject;
import endpoint.Index;
import endpoint.Json;

public class EntityUtils {

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	public static String getKind(Class<? extends DatastoreObject> clazz) {
		return clazz.getSimpleName();
	}

	public static void toEntity(DatastoreObject object, Entity entity) {
		Field[] fields = getFields(object.getClass());

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (isControl(field)) {
				continue;
			}

			if (isSaveAsList(field)) {
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
				if (isControl(field)) {
					continue;
				}

				if (isSaveAsList(field)) {
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
		Field[] allFields = ArrayUtils.addAll(DatastoreObject.class.getDeclaredFields(), clazz.getDeclaredFields());

		List<Field> fields = new ArrayList<Field>();

		for (int i = 0; i < allFields.length; i++) {
			Field field = allFields[i];
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			fields.add(field);
		}

		return fields.toArray(new Field[fields.size()]);
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

			if (isDate(field)) {
				return DateUtils.toTimestamp((String) value);
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

			if (isSaveAsJson(field)) {
				if (value == null) {
					return null;
				}
				return new Text(JsonUtils.to(value));
			}

			return value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T extends DatastoreObject> void setObjectProperty(T object, Entity entity, Field field) throws IllegalAccessException {
		field.setAccessible(true);

		Object value = entity.getProperty(field.getName());

		if (isEnum(field)) {
			setEnumProperty(object, field, value);
			return;
		}

		if (isSaveAsJson(field)) {
			setJsonProperty(object, field, value);
			return;
		}

		if (isInt(field)) {
			setIntProperty(object, field, value);
			return;
		}

		field.set(object, value);
	}

	private static <T extends DatastoreObject> void setIntProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, ((Long) value).intValue());
	}

	private static <T extends DatastoreObject> void setJsonProperty(T object, Field field, Object value) throws IllegalAccessException {
		if (value == null) {
			return;
		}

		String json = ((Text) value).getValue();

		if (isList(field)) {
			field.set(object, JsonUtils.fromArray(json, getListClass(field)));
		} else {
			field.set(object, JsonUtils.from(json, field.getType()));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends DatastoreObject> void setEnumProperty(T object, Field field, Object value) throws IllegalAccessException {
		if (value == null) {
			return;
		}

		field.set(object, Enum.valueOf((Class) field.getType(), value.toString()));
	}

	private static boolean isControl(Field field) {
		return Key.class.equals(field.getType()) || field.isSynthetic();
	}

	private static boolean isSaveAsJson(Field field) {
		return field.getAnnotation(Json.class) != null;
	}

	public static boolean isSaveAsList(Field field) {
		return isList(field) && !isSaveAsJson(field);
	}

	private static boolean isList(Field field) {
		return List.class.isAssignableFrom(field.getType());
	}

	private static boolean isDate(Field field) {
		return Date.class.isAssignableFrom(field.getType());
	}

	private static boolean isEnum(Object value) {
		return value != null && value.getClass().isEnum();
	}

	private static boolean isEnum(Field field) {
		return field.getType().isEnum();
	}

	private static boolean isInt(Field field) {
		return field.getType().getName().equals("int");
	}
}
