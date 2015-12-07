package io.yawp.driver.appengine;

import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectModel;
import io.yawp.repository.Repository;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class IdRefToKey {

    public static Key toKey(Repository r, IdRef<?> id) {
        return convertWithinRightNamespace(r, id.getClazz(), id);
    }

    private static Key convertWithinRightNamespace(Repository r, Class<?> clazz, IdRef<?> id) {
        r.namespace().set(clazz);
        try {
            Key parent = id.getParentId() == null ? null : toKey(r, id.getParentId());
            String kind = KindResolver.getKindFromClass(id.getClazz());
            if (id.getId() == null) {
                return KeyFactory.createKey(parent, kind, id.getName());
            }
            return KeyFactory.createKey(parent, kind, id.getId());

        } finally {
            r.namespace().reset();
        }
    }

    public static IdRef<?> toIdRef(Repository r, Key key, ObjectModel model) {
        Class<?> objectClass = model.getClazz();

        IdRef<?> idRef = null;
        if (key.getName() != null) {
            idRef = IdRef.create(r, objectClass, key.getName());
        } else {
            idRef = IdRef.create(r, objectClass, key.getId());
        }

        if (model.hasParent()) {
            idRef.setParentId(toIdRef(r, key.getParent(), model.getParentModel()));
        }
        return idRef;
    }

}
