package io.yawp.driver.mock;

import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MockStore {

	private static Map<IdRef<?>, Object> store = new LinkedHashMap<IdRef<?>, Object>();

	private static long nextId = 1;

	public static void put(IdRef<?> id, Object object) {
		store.put(id, object);
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

			if (parentId != null && !id.getParentId().equals(parentId)) {
				continue;
			}

			objects.add(get(id));

		}

		return objects;
	}

	public static long nextId() {
		return nextId++;
	}

	public static void clear() {
		nextId = 1;
		store.clear();
	}

}
