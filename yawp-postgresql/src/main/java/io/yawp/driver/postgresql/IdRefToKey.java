package io.yawp.driver.postgresql;

import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.driver.postgresql.datastore.Key;
import io.yawp.driver.postgresql.datastore.KeyFactory;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectModel;
import io.yawp.repository.Repository;

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

        if (key.getParent() != null) {
            idRef.setParentId(toIdRef(r, key.getParent(), createParentModel(r, key)));
        }
        return idRef;
    }

    private static ObjectModel createParentModel(Repository r, Key key) {
        String parentKind = key.getParent().getKind();
        Class<?> parentClazz = r.getClazzByKind(parentKind);
        return new ObjectModel(parentClazz);
    }

}

