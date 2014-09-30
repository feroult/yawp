package io.yawp.repository.transformers;

import io.yawp.repository.Feature;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

public abstract class Transformer<T> extends Feature {

	public Object defaults(T object) {
		return object;
	}

	public Object index(T object) {
		return defaults(object);
	}

	public Object show(T object) {
		return defaults(object);
	}

	public Object create(T object) {
		return defaults(object);
	}

	public Object update(T object) {
		return defaults(object);
	}

	public Object custom(T object) {
		return defaults(object);
	}

	protected Map<String, Object> asMap(Object object) {
		Map<String, Object> map = new HashMap<String, Object>();
		BeanMap beanMap = new BeanMap(object);
		for (Object key : beanMap.keySet()) {
			map.put(key.toString(), beanMap.get(key));
		}
		return map;
	}
}
