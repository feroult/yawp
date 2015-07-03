package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.servlet.HttpException;

import java.util.List;

public class Shield<T> extends Feature {

	private boolean allow = false;

	protected IdRef<?> parentId;

	protected IdRef<T> id;

	protected T object;

	protected List<T> objects;

	protected void always() {
	}

	protected void index() {
	}

	protected void show() {
	}

	protected void create() {
	}

	protected void update() {
	}

	protected void destroy() {
	}

	protected void custom() {
	}

	public final void protectIndex() {
		always();
		index();
		throwIfNotAllowed();
	}

	public final void protectShow() {
		always();
		show();
		throwIfNotAllowed();
	}

	public final void protectCreate() {
		always();

		if (isArray()) {
			protectCreateWithArray();
		} else {
			protectCreateWithObject();
		}

		throwIfNotAllowed();
	}

	private void protectCreateWithObject() {
		// TODO Auto-generated method stub

	}

	private void protectCreateWithArray() {
		// TODO Auto-generated method stub

	}

	public final void protectUpdate() {
		always();
		update();
		throwIfNotAllowed();
	}

	public final void protectDestroy() {
		always();
		destroy();
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

	@SuppressWarnings("unchecked")
	public void setObject(Object object) {
		this.object = (T) object;
	}

	@SuppressWarnings("unchecked")
	public void setObjects(List<?> objects) {
		this.objects = (List<T>) objects;
	}

	protected boolean isArray() {
		return objects != null;
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
