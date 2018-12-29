package io.yawp.servlet.cache;

import io.yawp.repository.IdRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {

	private static class C<T> extends ThreadLocal<CacheHolder<T>> {
		@Override
		protected CacheHolder<T> initialValue() {
			return new CacheHolder<>();
		}
	}

	private static final Map<Class<?>, C<?>> ms = new HashMap<>();

	public static <T> List<T> get(List<IdRef<T>> ids) {
		List<T> ts = new ArrayList<>();
		for (IdRef<T> id : ids) {
			ts.add(get(id));
		}
		return ts;
	}

	public static <T> T get(IdRef<T> t) {
		if (!ms.containsKey(t.getClazz())) {
			ms.put(t.getClazz(), new C<>());
		}
		return getTc(t).get().get(t);
	}

	@SuppressWarnings("unchecked")
	private static <T> C<T> getTc(IdRef<T> t) {
		return (C<T>) ms.get(t.getClazz());
	}

	public static void clearAll() {
		for (C<?> l : ms.values()) {
			l.get().clear();
		}
	}

}