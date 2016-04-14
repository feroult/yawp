package io.yawp.repository.transformers.hierarchy;

import io.yawp.repository.transformers.Transformer;

import java.util.Map;

public class AbstractTransformer<T> extends Transformer<T> {

    public Object allObjectsUpperCase(T o) {
        Map<String, Object> map = asMap(o);
        map.put("name", map.get("name").toString().toUpperCase());
        return map;
    }

}
