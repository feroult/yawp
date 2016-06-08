package io.yawp.driver.appengine;

import io.yawp.commons.utils.NameGenerator;
import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectModel;
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
            return createKey(id, parent, kind);

        } finally {
            r.namespace().reset();
        }
    }

    public static IdRef<?> toIdRef(Repository r, Key key, ObjectModel model) {
        Class<?> objectClass = model.getClazz();

        IdRef<?> idRef;

        if (model.isIdShuffled()) {
            idRef = getIdRefFromShuffledKey(r, key, objectClass);
        } else if (key.getName() != null) {
            idRef = IdRef.create(r, objectClass, key.getName());
        } else {
            idRef = IdRef.create(r, objectClass, key.getId());
        }

        if (key.getParent() != null) {
            idRef.setParentId(toIdRef(r, key.getParent(), createParentModel(r, key)));
        }
        return idRef;
    }

    private static Key createKey(IdRef<?> id, Key parent, String kind) {
        if (id.isShuffled()) {
            return createShuffledKey(id, parent, kind);
        }

        if (id.getId() == null) {
            return KeyFactory.createKey(parent, kind, id.getName());
        }

        return KeyFactory.createKey(parent, kind, id.getId());
    }

    private static Key createShuffledKey(IdRef<?> id, Key parent, String kind) {
        if (id.getId() == null) {
            return KeyFactory.createKey(parent, kind, NameGenerator.generateFromString(id.getName()));
        }
        return KeyFactory.createKey(parent, kind, NameGenerator.generateFromString(id.getId() + ""));
    }

    private static IdRef<?> getIdRefFromShuffledKey(Repository r, Key key, Class<?> objectClass) {
        IdRef<?> idRef;
        String name = NameGenerator.convertToString(key.getName());
        try {
            Long id = Long.valueOf(name);
            idRef = IdRef.create(r, objectClass, id);
        } catch (NumberFormatException e) {
            idRef = IdRef.create(r, objectClass, name);
        }
        return idRef;
    }

    private static ObjectModel createParentModel(Repository r, Key key) {
        String parentKind = key.getParent().getKind();
        Class<?> parentClazz = r.getClazzByKind(parentKind);
        return new ObjectModel(parentClazz);
    }

}
