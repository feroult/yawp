package io.yawp.repository.cache;

import io.yawp.commons.utils.json.gson.GsonJsonUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.hooks.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedEntityHook extends Hook<CachedEntity> {

    private static final GsonJsonUtils GSON = new GsonJsonUtils();
    public static final Map<String, List<CachedEntity>> cacheList = new HashMap<>();
    public static final Map<String, List<IdRef<CachedEntity>>> cacheIds = new HashMap<>();
    public static final Map<String, CachedEntity> cacheFetch = new HashMap<>();

    @Override
    public void beforeQuery(BeforeQueryObject<CachedEntity> obj) {
        if (obj.getQuery().hasCursor() || obj.getQuery().hasForcedResponse()) {
            return;
        }

        String key = GSON.to(obj.getQuery().toMap());
        switch (obj.getType()) {
            case LIST:
                if (cacheList.containsKey(key)) {
                    obj.getQuery().forceResponseList(cacheList.get(key));
                }
                break;
            case IDS:
                if (cacheIds.containsKey(key)) {
                    obj.getQuery().forceResponseIds(cacheIds.get(key));
                }
                break;
            case FETCH:
                if (cacheFetch.containsKey(key)) {
                    obj.getQuery().forceResponseFetch(cacheFetch.get(key));
                }
                break;
        }
    }

    @Override
    public void afterQuery(AfterQueryFetchObject<CachedEntity> obj) {
        String key = GSON.to(obj.getQuery().toMap());
        cacheFetch.put(key, obj.getElement());
    }

    @Override
    public void afterQuery(AfterQueryListObject<CachedEntity> obj) {
        String key = GSON.to(obj.getQuery().toMap());
        cacheList.put(key, obj.getList());
    }

    @Override
    public void afterQuery(AfterQueryIdsObject<CachedEntity> obj) {
        String key = GSON.to(obj.getQuery().toMap());
        cacheIds.put(key, obj.getIds());
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
        cacheFetch.clear();
        cacheIds.clear();
        cacheList.clear();
    }
}
