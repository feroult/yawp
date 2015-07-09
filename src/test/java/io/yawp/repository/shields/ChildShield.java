package io.yawp.repository.shields;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.parents.ShieldedChild;

public class ChildShield extends Shield<ShieldedChild> {

	@Override
	public void index(IdRef<?> parentId) {
		allow(isId100(parentId));
	}

	@Override
	public void show(IdRef<ShieldedChild> id) {
		allow(isId100(id));
	}

	private boolean isId100(IdRef<?> id) {
		return id.asLong().equals(100l);
	}
}
