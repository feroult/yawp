package io.yawp.commons.utils;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.kind.DefaultKindResolver;
import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.actions.Action;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;
import io.yawp.repository.annotations.ParentId;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.shields.Shield;

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
import com.google.appengine.api.datastore.Text;

// TODO move to repository package
// TODO make it not static and repository aware and smaller, very, very smaller
public class EntityUtils {

	private static final String KINDRESOLVER_SETTING_KEY = "yawp.kindresolver";

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	private static KindResolver kindResolver;

	static {
		loadKindResolver();
	}

	private static void loadKindResolver() {
		String kindResolverClazzName = System.getProperty(KINDRESOLVER_SETTING_KEY);
		if (kindResolverClazzName == null) {
			kindResolver = new DefaultKindResolver();
			return;
		}
		try {
			kindResolver = (KindResolver) Class.forName(kindResolverClazzName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException("Invalid kind resolver: " + kindResolverClazzName, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getHookObject(Class<? extends Hook<T>> hook) {
		return (Class<T>) ReflectionUtils.getGenericParameter(hook);
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getShieldObject(Class<? extends Shield<T>> hook) {
		return (Class<T>) ReflectionUtils.getGenericParameter(hook);
	}

	public static String getKindFromClass(Class<?> clazz) {
		return kindResolver.getKind(clazz);
	}

	public static Class<?> getClassFromKind(Repository r, String kind) {
		String endpointPath = kindResolver.getPath(kind);
		return r.getFeatures().get(endpointPath).getClazz();
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
			throw new RuntimeException(e);
		}
	}

	public static void setId(Object object, IdRef<?> id) {
		Field idField = getAnnotatedIdFromClass(object.getClass());
		try {
			idField.set(object, id);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		// setParentId(object, id.getParentId());
	}

	public static IdRef<?> getParentId(Object object) {
		Field parentField = EntityUtils.getAnnotatedParentFromClass(object.getClass());
		if (parentField == null) {
			return null;
		}

		try {
			return (IdRef<?>) parentField.get(object);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Key getParentKey(Object object) {
		IdRef<?> parentIdRef = getParentId(object);
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
		IdRef<?> idRef = getId(object);
		if (idRef == null) {
			return null;
		}
		return idRef.asKey();

	}

	public static IdRef<?> getId(Object object) {
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
		IdRef<?> idRef = getId(object);
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

	public static IdRef<?> convertToIdRef(Repository r, String id) {
		return IdRef.parse(r, HttpVerb.GET, id);
	}

	public static List<IdRef<?>> convertToIdRefs(Repository r, List<?> rawIds) {
		List<IdRef<?>> ids = new ArrayList<>();
		for (Object rawId : rawIds) {
			if (rawId instanceof String) {
				ids.add(convertToIdRef(r, (String) rawId));
			} else {
				ids.add((IdRef<?>) rawId);
			}
		}
		return ids;
	}

	public static <T> String getActualFieldName(String fieldName, Class<T> clazz) {
		Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);

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
		Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);

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

			if (isSaveAsText(field)) {
				return new Text(value.toString());
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

		if (isSaveAsText(field)) {
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

	public static boolean isId(Class<?> clazz, String fieldName) {
		try {
			return isId(clazz.getDeclaredField(fieldName));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isId(Field field) {
		return field.getAnnotation(Id.class) != null;
	}

	public static boolean hasIndex(Class<?> clazz, String fieldName) {
		return hasIndex(ReflectionUtils.getFieldRecursively(clazz, fieldName));
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

	private static boolean isSaveAsText(Field field) {
		return field.getAnnotation(io.yawp.repository.annotations.Text.class) != null;
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

	public static Class<?> getParentClazz(Class<?> endpointClazz) {
		Field field = getAnnotatedParentFromClass(endpointClazz);
		if (field == null) {
			return null;
		}
		return (Class<?>) getParametrizedTypes(field)[0];
	}

	public static Class<?> getAncestorClazz(int ancestor, Class<?> endpointClazz) {
		Class<?> parentClazz = endpointClazz;
		for (int i = 0; i <= ancestor; i++) {
			parentClazz = getParentClazz(parentClazz);
		}
		return parentClazz;
	}

	public static int getAncestorNumber(Class<? extends Object> endpointClazz, Class<?> ancestorClazz) {
		if (endpointClazz.equals(ancestorClazz)) {
			return -1;
		}

		Class<?> parentClazz = getParentClazz(endpointClazz);
		int ancestorNumber = 0;

		while (parentClazz != null && !parentClazz.equals(ancestorClazz)) {
			parentClazz = getParentClazz(parentClazz);
			ancestorNumber++;
		}

		if (parentClazz == null) {
			throw new RuntimeException("Invalid ancestor " + ancestorClazz.getName() + " for class " + endpointClazz.getName());
		}

		return ancestorNumber;
	}

}
