package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.ActionKey;
import io.yawp.servlet.HttpException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public abstract class ShieldBase<T> extends Feature {

	private boolean allow = false;

	private IdRef<T> id;

	private T object;

	private List<T> objects;

	private Map<String, String> params;

	private ActionKey actionKey;

	private Map<ActionKey, Method> actionMethods;

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

	protected final boolean requestHasAnyObject() {
		return object != null || (objects != null && objects.size() > 0);
	}

	protected final boolean requestHasObjectArray() {
		return objects != null;
	}

	public final void protectIndex() {
		always();
		index(id);
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
	public final void setId(IdRef<?> id) {
		this.id = (IdRef<T>) id;
	}

	@SuppressWarnings("unchecked")
	public final void setObject(Object object) {
		this.object = (T) object;
	}

	@SuppressWarnings("unchecked")
	public final void setObjects(List<?> objects) {
		this.objects = (List<T>) objects;
	}

	public final void setParams(Map<String, String> params) {
		this.params = params;
	}

	public final void setActionKey(ActionKey actionKey) {
		this.actionKey = actionKey;
	}

	public final void setActionMethods(Map<ActionKey, Method> actionMethods) {
		this.actionMethods = actionMethods;
	}

	private void annotadedCustoms() {
		if (!actionMethods.containsKey(actionKey)) {
			return;
		}

		Method method = actionMethods.get(actionKey);
		invokeCustomActionShield(method);
	}

	private void invokeCustomActionShield(Method method) {
		try {
			Object[] arguments = ActionKey.getActionMethodParameters(method, id, params);
			method.invoke(this, arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
