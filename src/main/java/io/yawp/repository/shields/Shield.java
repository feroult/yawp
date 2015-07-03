package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.servlet.HttpException;

public class Shield<T> extends Feature {

	private boolean allow = false;

	protected void defaults() {
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

	protected void destroy(IdRef<T> id) {
		defaults();
	}

	protected void custom() {
		defaults();
	}

	public final void protectIndex() {
		index(null);
		throwIfNotAllowed();
	}

	public final void protectShow() {
		show(null);
		throwIfNotAllowed();
	}

	public final void protectCreate() {
		create(null);
		throwIfNotAllowed();
	}

	public final void protectUpdate() {
		update(null);
		throwIfNotAllowed();
	}

	public final void protectDestroy() {
		destroy(null);
		throwIfNotAllowed();
	}

	public final void protectCustom() {
		custom();
		throwIfNotAllowed();
	}

	protected final Shield<T> allow(boolean condition) {
		this.allow = this.allow || condition;
		return this;
	}

	private void throwIfNotAllowed() {
		if (!allow) {
			throw new HttpException(404);
		}
	}

	// TODO

	protected Shield<T> allow() {
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
