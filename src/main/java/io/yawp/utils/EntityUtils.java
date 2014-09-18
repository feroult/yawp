package io.yawp.utils;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.actions.Action;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;
import io.yawp.repository.annotations.ParentId;
import io.yawp.repository.hooks.Hook;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

// TODO move to repository package
// TODO make it not static and repository aware and smaller, very, very smaller
public class EntityUtils {

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getHookObject(Class<? extends Hook<T>> hook) {
		return (Class<T>) ReflectionUtils.getGenericParameter(hook);
	}

	public static String getKindFromClass(Class<?> clazz) {
		return clazz.getCanonicalName();
	}

	public static Class<?> getClassFromKind(String kind) {
		try {
			return Class.forName(kind);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Kind not related to any class: " + kind);
		}
	}

	public static void toEntity(Object object, Entity entity) {
		List<Field> fields = ReflectionUtils.getFieldsRecursively(object.getClass());

		for (Field field : fields) {
			if (isControl(field) || isSaveAsList(field)) {
				continue;
			}

			setEntityProperty(object, entity, field);
		}
	}

	public static Class<?> getIdType(Class<?> clazz) {
		Field idField = getFieldWithAnnotation(clazz, Id.class);
		if (!idField.getType().equals(IdRef.class)) {
			// TODO remove long id support
			return clazz;
		}
		ParameterizedType type = (ParameterizedType) idField.getGenericType();
		Type[] types = type.getActualTypeArguments();
		assert types.length == 1;
		return (Class<?>) types[0];
	}

	public static void setParentId(Object object, IdRef<?> parentId) {
		Field parentIdField = getAnnotatedParentFromClass(object.getClass());
		if (parentIdField == null) {
			if (parentId != null) {
				throw new RuntimeException("Trying to set parentId " + parentId + " to class " + object.getClass().getSimpleName()
						+ ", but it doesn't seem to have a @Parent field.");
			}
			return;
		}

		try {
			parentIdField.set(object, parentId);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}

	public static void setId(Object object, IdRef<?> id) {
		Field idField = getAnnotatedIdFromClass(object.getClass());
		try {
			idField.set(object, id);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Unexpected exception", e);
		}
		setParentId(object, id.getParentId());
	}

	public static IdRef<?> getParentIdRef(Object object) {
		Field parentField = EntityUtils.getAnnotatedParentFromClass(object.getClass());
		if (parentField != null) {
			try {
				return (IdRef<?>) parentField.get(object);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("Unexpected error.", e);
			}
		}
		return null;
	}

	public static Key getParentKey(Object object) {
		IdRef<?> parentIdRef = getParentIdRef(object);
		if (parentIdRef == null) {
			return null;
		}
		return parentIdRef.asKey();
	}

	public static <T> T toObject(Repository r, Entity entity, Class<T> clazz) {
		try {
			Constructor<T> defaultConstructor = clazz.getDeclaredConstructor(new Class<?>[] {});
			defaultConstructor.setAccessible(true);
			T object = defaultConstructor.newInstance();
			setKey(r, object, entity.getKey());
			List<Field> fields = ReflectionUtils.getFieldsRecursively(clazz);

			for (Field field : fields) {
				field.setAccessible(true);
				if (isControl(field) || isSaveAsList(field)) {
					continue;
				}

				setObjectProperty(r, object, entity, field);
			}

			return object;
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

	public static <T> void setKey(Repository r, T object, Key key) {
		try {
			Field idField = getIdField(object.getClass());
			idField.set(object, IdRef.fromKey(r, key));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Key getKey(Object object) {
		IdRef<?> idRef = getIdRef(object);
		if (idRef == null) {
			return null;
		}
		return idRef.asKey();

	}

	private static IdRef<?> getIdRef(Object object) {
		Field field = getIdField(object.getClass());

		if (!isIdRef(field)) {
			throw new RuntimeException("@Id must be " + IdRef.class.getSimpleName());
		}

		try {

			IdRef<?> id = (IdRef<?>) field.get(object);
			return id;

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getIdFieldName(Class<?> clazz) {
		return getIdField(clazz).getName();
	}

	public static Class<?> getIdFieldRefClazz(Class<?> clazz) {
		Field idField = getIdField(clazz);
		return (Class<?>) getParametrizedTypes(idField)[0];
	}

	private static Field getIdField(Class<?> clazz) {
		Field field = getAnnotatedIdFromClass(clazz);
		if (field == null) {
			throw new RuntimeException("No @Id annotated field found in class " + clazz.getSimpleName());
		}
		return field;
	}

	private static Field getAnnotatedIdFromClass(Class<?> clazz) {
		return getFieldWithAnnotation(clazz, Id.class);
	}

	public static Field getAnnotatedParentFromClass(Class<?> clazz) {
		return getFieldWithAnnotation(clazz, ParentId.class);
	}

	private static Field getFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		Field theField = null;
		for (Field field : ReflectionUtils.getFieldsRecursively(clazz)) {
			if (field.isAnnotationPresent(annotationClass)) {
				if (theField != null) {
					throw new RuntimeException("You can have at most one field annotated with the " + annotationClass.getSimpleName()
							+ " class.");
				}
				theField = field;
				theField.setAccessible(true);
			}
		}

		return theField;
	}

	public static Object getIdSimpleValue(Object object) {
		IdRef<?> idRef = getIdRef(object);
		return idRef.getSimpleValue();
	}

	public static Class<?> getListType(Field field) {
		return (Class<?>) getParametrizedTypes(field)[0];
	}

	private static Type[] getParametrizedTypes(Field field) {
		Type genericFieldType = field.getGenericType();
		if (genericFieldType instanceof ParameterizedType) {
			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			return fieldArgTypes;
		}

		throw new RuntimeException("Can't get generic type");
	}

	private static Field getFieldFromAnyParent(Class<?> clazz, String fieldName) {
		while (clazz != null) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ex) {
				clazz = clazz.getSuperclass();
			}
		}

		throw new RuntimeException("Field '" + fieldName + "'not found in entity " + clazz, new NoSuchFieldException(fieldName));
	}

	public static <T> String getActualFieldName(String fieldName, Class<T> clazz) {
		Field field = getFieldFromAnyParent(clazz, fieldName);

		if (isKey(field)) {
			return Entity.KEY_RESERVED_PROPERTY;
		}

		if (isIndexNormalizable(field)) {
			return NORMALIZED_FIELD_PREFIX + fieldName;
		}

		return fieldName;
	}

	private static boolean isKey(Field field) {
		return field.getAnnotation(Id.class) != null;
	}

	private static Index getIndex(Field field) {
		Index index = field.getAnnotation(Index.class);
		if (index == null) {
			throw new RuntimeException("You must add @Index annotation the the field '" + field.getName()
					+ "' if you want to use it as a index in where statements.");
		}
		return index;
	}

	public static <T> Object getActualFieldValue(String fieldName, Class<T> clazz, Object value) {
		Field field = getFieldFromAnyParent(clazz, fieldName);

		if (isCollection(value)) {
			return getActualListFieldValue(fieldName, clazz, (Collection<?>) value);
		}

		if (isKey(field)) {
			return getActualKeyFieldValue(clazz, value);
		}

		if (isEnum(value)) {
			return value.toString();
		}

		if (isIndexNormalizable(field)) {
			return normalizeValue(value);
		}

		if (value instanceof IdRef) {
			return ((IdRef<?>) value).getUri();
		}

		if (isDate(field) && value instanceof String) {
			return DateUtils.toTimestamp((String) value);
		}

		return value;
	}

	private static boolean isCollection(Object value) {
		return Collection.class.isInstance(value);
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

	public static Key createKey(IdRef<?> id) {
		if (id == null) {
			return null;
		}
		Key parentKey = createKey(id.getParentId());
		return createKey(parentKey, id.asLong(), id.getClazz());
	}

	public static Key createKey(Long id, Class<?> clazz) {
		return KeyFactory.createKey(getKindFromClass(clazz), id);
	}

	public static Key createKey(Key parentKey, Long id, Class<?> clazz) {
		return KeyFactory.createKey(parentKey, getKindFromClass(clazz), id);
	}

	private static void setEntityProperty(Object object, Entity entity, Field field) {
		Object value = getFieldValue(field, object);

		if (!hasIndex(field)) {
			entity.setUnindexedProperty(field.getName(), value);
			return;
		}

		if (isIndexNormalizable(field)) {
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

			if (value == null) {
				return null;
			}

			if (isEnum(value)) {
				return value.toString();
			}

			if (isSaveAsJson(field)) {
				return new Text(JsonUtils.to(value));
			}

			if (isIdRef(field)) {
				IdRef<?> idRef = (IdRef<?>) value;
				return idRef.getUri();
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

	private static <T> void setObjectProperty(Repository r, T object, Entity entity, Field field) throws IllegalAccessException {
		Object value = entity.getProperty(field.getName());

		if (value == null) {
			field.set(object, null);
			return;
		}

		if (isEnum(field)) {
			setEnumProperty(object, field, value);
			return;
		}

		if (isSaveAsJson(field)) {
			setJsonProperty(r, object, field, value);
			return;
		}

		if (isInt(field)) {
			setIntProperty(object, field, value);
			return;
		}

		if (isIdRef(field)) {
			setIdRefProperty(r, object, field, value);
			return;
		}

		field.set(object, value);
	}

	public static int listSize(Object value) {
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

	private static <T> void setJsonProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
		if (value == null) {
			return;
		}

		String json = ((Text) value).getValue();
		field.set(object, JsonUtils.from(r, json, field.getGenericType()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> void setEnumProperty(T object, Field field, Object value) throws IllegalAccessException {
		if (value == null) {
			return;
		}

		field.set(object, Enum.valueOf((Class) field.getType(), value.toString()));
	}

	private static boolean hasIndex(Field field) {
		return field.getAnnotation(Index.class) != null;
	}

	private static boolean isIndexNormalizable(Field field) {
		return getIndex(field).normalize() && isString(field);
	}

	private static boolean isControl(Field field) {
		return Key.class.equals(field.getType()) || field.isAnnotationPresent(Id.class);
	}

	private static boolean isIdRef(Field field) {
		return IdRef.class.isAssignableFrom(field.getType());
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

	private static boolean isString(Field field) {
		return String.class.isAssignableFrom(field.getType());
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Class<?> getActionEndpoint(Method action) {
		Class<?> declaringClass = action.getDeclaringClass();
		assert Action.class.isAssignableFrom(declaringClass);
		return getActionEndpoint((Class<? extends Action>) declaringClass);
	}

	@SuppressWarnings("unchecked")
	private static <T, V extends Action<T>> Class<T> getActionEndpoint(Class<V> clazz) {
		return (Class<T>) ReflectionUtils.getGenericParameter(clazz);
	}

	public static Class<?> getParentClass(Class<?> endpoint) {
		Field field = getAnnotatedParentFromClass(endpoint);
		if (field == null) {
			return null;
		}
		return (Class<?>) getParametrizedTypes(field)[0];
	}
}
