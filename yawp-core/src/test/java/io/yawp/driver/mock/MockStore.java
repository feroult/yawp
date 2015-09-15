package io.yawp.driver.mock;

import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.LRUMap;

public class MockStore {

	private static Map<IdRef<?>, Object> store = new LinkedHashMap<IdRef<?>, Object>();

	private static long nextId = 1;

	private static LRUMap cursors = new LRUMap(10);

	public static void put(IdRef<?> id, Object object) {
		Object clone = cloneBean(object);
		store.put(id, clone);
	}

	public static Object get(IdRef<?> id) {
		return store.get(id);
	}

	public static void remove(IdRef<?> id) {
		store.remove(id);
	}

	public static List<Object> list(Class<?> clazz, IdRef<?> parentId) {
		List<Object> objects = new ArrayList<Object>();

		for (Object object : store.values()) {
			ObjectHolder objectHolder = new ObjectHolder(object);

			IdRef<?> id = objectHolder.getId();

			if (!id.getClazz().equals(clazz)) {
				continue;
			}

			if (!isAncestor(id, parentId)) {
				continue;
			}

			objects.add(get(id));
		}

		return objects;
	}

	private static boolean isAncestor(IdRef<?> id, IdRef<?> parentId) {
		if (parentId == null) {
			return true;
		}

		IdRef<?> currentParentId = id.getParentId();
		while (currentParentId != null) {
			if (currentParentId.equals(parentId)) {
				return true;
			}
			currentParentId = currentParentId.getParentId();
		}
		return false;
	}

	public static long nextId() {
		return nextId++;
	}

	public static void clear() {
		nextId = 1;
		store.clear();
	}

	public static String createCursor(Object object) {
		ObjectHolder objectHolder = new ObjectHolder(object);
		String cursor = UUID.randomUUID().toString();
		cursors.put(cursor, objectHolder.getId());
		return cursor;
	}

	public static IdRef<?> getCursor(String cursor) {
		return (IdRef<?>) cursors.get(cursor);
	}

	public static void updateCursor(String cursor, Object object) {
		ObjectHolder objectHolder = new ObjectHolder(object);
		cursors.put(cursor, objectHolder.getId());
	}

	private static Object cloneBean(Object object) {
		try {
			return BeanUtils.cloneBean(object);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
