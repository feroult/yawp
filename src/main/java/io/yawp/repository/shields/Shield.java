package io.yawp.repository.shields;

import io.yawp.repository.IdRef;

import java.util.List;

public abstract class Shield<T> extends ShieldBase<T> {

	public void always() {
	}

	public void index(IdRef<?> parentId) {
	}

	public void show(IdRef<T> id) {
	}

	public void create(List<T> objects) {
	}

	public void update(IdRef<T> id, T object) {
	}

	public void destroy(IdRef<T> id) {
	}

	public void custom() {
	}

}
