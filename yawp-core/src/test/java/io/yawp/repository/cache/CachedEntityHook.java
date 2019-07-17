package io.yawp.repository.cache;

import io.yawp.commons.utils.json.gson.GsonJsonUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryType;

import java.util.HashMap;
import java.util.Map;

public class CachedEntityHook extends Hook<CachedEntity> {

    private static final GsonJsonUtils GSON = new GsonJsonUtils();

    public static final Map<QueryType, Map<String, Object>> caches = new HashMap<>();

    static {
        caches.put(QueryType.LIST, new HashMap<>());
        caches.put(QueryType.FETCH, new HashMap<>());
        caches.put(QueryType.IDS, new HashMap<>());
    }

    @Override
    public void beforeQuery(QueryBuilder<CachedEntity> q) {
        if (q.hasCursor() || q.hasForcedResponse()) {
            return;
        }

        String key = GSON.to(q.toMap());
        Map<String, Object> cache = caches.get(q.getExecutedQueryType());
        if (cache.containsKey(key)) {
            q.forceResult(q.getExecutedQueryType(), cache.get(key));
        }
    }

    @Override
    public void afterQuery(QueryBuilder<CachedEntity> q) {
        String key = GSON.to(q.toMap());
        Map<String, Object> cache = caches.get(q.getExecutedQueryType());
        cache.put(key, q.getExecutedResponse());
    }

    @Override
    public void afterSave(CachedEntity object) {
        clearAll();
    }

    @Override
    public void afterDestroy(IdRef<CachedEntity> object) {
        clearAll();
    }

    private void clearAll() {
        for (Map<String, Object> cache : caches.values()) {
            cache.clear();
        }
    }
}
