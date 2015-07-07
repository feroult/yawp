package io.yawp.repository.shields;

import java.util.List;

import io.yawp.repository.IdRef;


public class Shield<T> extends ShieldBase<T> {

	public void always() {
	}

	public void index(IdRef<?> parentId) {
	}

	public void show(IdRef<T> id) {
	}

	public void create(T object, List<T> objects) {
	}

	public void update(IdRef<T> id, T object) {
	}

	public void destroy(IdRef<T> id) {
	}

	public void custom() {
	}

}
