package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.servlet.HttpException;

public class Shield<T> extends Feature {

	private boolean allow = false;

	private IdRef<T> id;

	protected void always() {
	}

	protected void index(IdRef<?> parentId) {
	}

	protected void show(IdRef<T> id) {
	}

	protected void create(T object) {
	}

	protected void update(IdRef<T> id, T object) {
	}

	protected void destroy(IdRef<T> id) {
	}

	protected void custom() {
	}

	public final void protectIndex() {
		always();
		index(null);
		throwIfNotAllowed();
	}

	public final void protectShow() {
		always();
		show(id);
		throwIfNotAllowed();
	}

	public final void protectCreate() {
		always();
		create(null);
		throwIfNotAllowed();
	}

	public final void protectUpdate() {
		always();
		update(id, null);
		throwIfNotAllowed();
	}

	public final void protectDestroy() {
		always();
		destroy(id);
		throwIfNotAllowed();
	}

	public final void protectCustom() {
		always();
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

	@SuppressWarnings("unchecked")
	public void setId(IdRef<?> id) {
		this.id = (IdRef<T>) id;
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
