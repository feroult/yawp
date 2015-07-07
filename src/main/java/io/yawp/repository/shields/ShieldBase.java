package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.ActionKey;
import io.yawp.servlet.HttpException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class ShieldBase<T> extends Feature {

	private boolean allow = false;

	private IdRef<?> parentId;

	private IdRef<T> id;

	private T object;

	private List<T> objects;

	private ActionKey actionKey;

	public abstract void always();

	public abstract void index(IdRef<?> parentId);

	public abstract void show(IdRef<T> id);

	public abstract void create(T object, List<T> objects);

	public abstract void update(IdRef<T> id, T object);

	public abstract void destroy(IdRef<T> id);

	public abstract void custom();

	protected ShieldBase<T> allow() {
		return allow(true);
	}

	protected final ShieldBase<T> allow(boolean condition) {
		this.allow = this.allow || condition;
		return this;
	}

	public final void protectIndex() {
		always();
		index(parentId);
		throwIfNotAllowed();
	}

	public final void protectShow() {
		always();
		show(id);
		throwIfNotAllowed();
	}

	public final void protectCreate() {
		always();
		create(object, objects);
		throwIfNotAllowed();
	}

	public final void protectUpdate() {
		always();
		update(id, object);
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
		annotadedCustoms();
		throwIfNotAllowed();
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

	public void setActionKey(ActionKey actionKey) {
		this.actionKey = actionKey;
	}

	protected boolean requestHasObject() {
		return object != null || (objects != null && objects.size() > 0);
	}

	protected boolean isArray() {
		return objects != null;
	}

	protected void annotadedCustoms() {
		Method[] methods = getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (!methodIsForAction(method)) {
				continue;
			}
			protectCustomAction(method);
		}
	}

	private boolean methodIsForAction(Method method) {
		return actionKey.getVerb().hasAnnotation(method) && sameActionName(method);
	}

	private void protectCustomAction(Method method) {
		try {
			method.invoke(this);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean sameActionName(Method method) {
		return actionKey.getVerb().getAnnotationValue(method).equals(actionKey.getActionName());
	}
}
