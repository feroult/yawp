package endpoint.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import endpoint.Id;
import endpoint.Index;
import endpoint.Json;

public class EntityUtils {

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	public static String getKind(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	public static void toEntity(Object object, Entity entity) {
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

	public static <T> T toObject(Entity entity, Class<T> clazz) {
		try {
			T object = clazz.newInstance();

			setKey(object, entity.getKey());

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

	public static <T> void setKey(T object, Key key) {
		try {
			Field field = getAnnotatedId(object);

			if (field != null) {
				field.setAccessible(true);
				field.set(object, key.getId());
				return;
			}

			BeanUtils.setProperty(object, "key", key);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static Key getKey(Object object) {
		try {
			Field field = getAnnotatedId(object);

			if (field != null) {
				field.setAccessible(true);
				if (field.get(object) == null) {
					return null;
				}
				return createKey((Long) field.get(object), object.getClass());
			}

			return (Key) PropertyUtils.getProperty(object, "key");

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private static Field getAnnotatedId(Object object) {
		Class<?> clazz = object.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}

	public static Long getId(Object object) {
		return getKey(object).getId();
	}

	public static <T> Field[] getFields(Class<T> clazz) {
		Field[] allFields = ArrayUtils.addAll(Object.class.getDeclaredFields(), clazz.getDeclaredFields());

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

	public static Class<?> getParametrizedType(Field field) {
		return (Class<?>) getParametrizedTypes(field)[0];
	}

	private static Type[] getParametrizedTypes(Field field) {
		Type genericFieldType = field.getGenericType();
		if (genericFieldType instanceof ParameterizedType) {
			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			return fieldArgTypes;
		}

		return null;
	}

	public static <T> String getIndexFieldName(String fieldName, Class<T> clazz) {
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

	public static <T> Object getIndexFieldValue(String fieldName, Class<T> clazz, Object value) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			Index index = field.getAnnotation(Index.class);

			if (index.normalize()) {
				return normalizeValue(value);
			}

			if (isDate(field) && value instanceof String) {
				return DateUtils.toTimestamp((String) value);
			}

			return value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Key createKey(Long id, Class<?> clazz) {
		return KeyFactory.createKey(getKind(clazz), id);
	}

	private static void setEntityProperty(Object object, Entity entity, Field field) {
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

		return StringUtils.stripAccents((String) o).toLowerCase();
	}

	private static Object getFieldValue(Field field, Object object) {
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

	public static Object getter(Object o, String property) {
		try {
			if (Map.class.isInstance(o)) {
				return ((Map<?, ?>) o).get(property);
			}

			return new PropertyDescriptor(property, o.getClass()).getReadMethod().invoke(o);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> void setObjectProperty(T object, Entity entity, Field field) throws IllegalAccessException {
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

	private static <T> void setIntProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, ((Long) value).intValue());
	}

	private static <T> void setJsonProperty(T object, Field field, Object value) throws IllegalAccessException {
		if (value == null) {
			return;
		}

		String json = ((Text) value).getValue();

		if (isList(field)) {
			field.set(object, JsonUtils.fromList(json, getParametrizedType(field)));
		} else if (isMap(field)) {
			Type[] types = getParametrizedTypes(field);
			field.set(object, JsonUtils.fromMap(json, types[0], types[1]));
		} else {
			field.set(object, JsonUtils.from(json, field.getType()));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> void setEnumProperty(T object, Field field, Object value) throws IllegalAccessException {
		if (value == null) {
			return;
		}

		field.set(object, Enum.valueOf((Class) field.getType(), value.toString()));
	}

	private static boolean isControl(Field field) {
		return Key.class.equals(field.getType()) || field.isAnnotationPresent(Id.class) || field.isSynthetic();
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

	private static boolean isMap(Field field) {
		return Map.class.isAssignableFrom(field.getType());
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
		return Integer.class.isAssignableFrom(field.getType()) || field.getType().getName().equals("int");
	}
}
