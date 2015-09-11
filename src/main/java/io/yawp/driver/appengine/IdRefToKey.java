package io.yawp.driver.appengine;

import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
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

	public static IdRef<?> toIdRef(Repository r, Key key) {
		if (key == null) {
			return null;
		}
		Class<?> objectClass = KindResolver.getClassFromKind(r, key.getKind());

		IdRef<?> ref = null;
		if (key.getName() != null) {
			ref = IdRef.create(r, objectClass, key.getName());
		} else {
			ref = IdRef.create(r, objectClass, key.getId());
		}
		ref.setParentId(toIdRef(r, key.getParent()));
		return ref;
	}
}
