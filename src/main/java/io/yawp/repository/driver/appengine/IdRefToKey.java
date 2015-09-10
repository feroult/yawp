package io.yawp.repository.driver.appengine;

import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class IdRefToKey {

	public static Key convert(IdRef<?> id) {
		Repository r = id.getRepository();
		Class<?> clazz = id.getClazz();

		return convertWithinRightNamespace(id, r, clazz);
	}

	private static Key convertWithinRightNamespace(IdRef<?> id, Repository r, Class<?> clazz) {
//		r.namespace().set(clazz);
//		try {
			Key parent = id.getParentId() == null ? null : convert(id.getParentId());
			String kind = KindResolver.getKindFromClass(id.getClazz());
			if (id.getId() == null) {
				return KeyFactory.createKey(parent, kind, id.getName());
			}
			return KeyFactory.createKey(parent, kind, id.getId());

//		} finally {
//			r.namespace().reset();
//		}
	}
}
