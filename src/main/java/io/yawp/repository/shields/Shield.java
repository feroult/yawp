package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.servlet.HttpException;

public class Shield<T> extends Feature {

	protected void defaults() {
		deny();
	}

	protected void index(IdRef<?> parentId) {
		defaults();
	}

	protected void show(IdRef<T> id) {
		defaults();
	}

	protected void create(T object) {
		defaults();
	}

	protected void update(T object) {
		defaults();
	}

	protected void delete(IdRef<T> id) {
		defaults();
	}

	public final void protectIndex() {
		throw new HttpException(404);
	}

	// TODO

	protected void deny() {
		// TODO Auto-generated method stub

	}

	protected Shield<T> allow() {
		// TODO Auto-generated method stub
		return this;
	}

	protected Shield<T> allow(boolean rh) {
		// TODO Auto-generated method stub
		return this;
	}

	public void where(String string, String string2, int i) {
		// TODO Auto-generated method stub

	}

	public void facade(Class<?> clazz) {
		// TODO Auto-generated method stub

	}

}
