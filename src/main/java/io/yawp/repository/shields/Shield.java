package io.yawp.repository.shields;

import io.yawp.repository.IdRef;

import java.util.List;

public abstract class Shield<T> extends ShieldBase<T> {

	public void always() {
	}

	public void defaults() {
	}

	public void index(IdRef<?> parentId) {
		defaults();
	}

	public void show(IdRef<T> id) {
		defaults();
	}

	public void create(List<T> objects) {
		defaults();
	}

	public void update(IdRef<T> id, T object) {
		defaults();
	}

	public void destroy(IdRef<T> id) {
		defaults();
	}

	public void custom() {

	}

}
