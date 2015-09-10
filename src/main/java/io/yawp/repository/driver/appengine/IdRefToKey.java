package io.yawp.repository.driver.appengine;

import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class IdRefToKey {

	public static Key convert(Repository r, IdRef<?> id) {
		return convertWithinRightNamespace(r, id.getClazz(), id);
	}

	private static Key convertWithinRightNamespace(Repository r, Class<?> clazz, IdRef<?> id) {
		r.namespace().set(clazz);
		try {
			Key parent = id.getParentId() == null ? null : convert(r, id.getParentId());
			String kind = KindResolver.getKindFromClass(id.getClazz());
			if (id.getId() == null) {
				return KeyFactory.createKey(parent, kind, id.getName());
			}
			return KeyFactory.createKey(parent, kind, id.getId());

		} finally {
			r.namespace().reset();
		}
	}
}
