package io.yawp.repository.transformers;

import io.yawp.repository.Feature;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

public class Transformer<T> extends Feature {

	protected Map<String, Object> asMap(Object object) {
		Map<String, Object> map = new HashMap<String, Object>();
		BeanMap beanMap = new BeanMap(object);
		for (Object key : beanMap.keySet()) {
			map.put(key.toString(), beanMap.get(key));
		}
		return map;
	}

}
