package io.yawp.repository.shields;

import io.yawp.commons.http.HttpVerb;
import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.ActionKey;
import io.yawp.servlet.HttpException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Shield<T> extends Feature {

	private boolean allow = false;

	protected IdRef<?> parentId;

	protected IdRef<T> id;

	protected T object;

	protected List<T> objects;

	private ActionKey actionKey;

	public void always() {
	}

	public void index() {
	}

	public void show() {
	}

	public void create() {
	}

	public void update() {
	}

	public void destroy() {
	}

	public void custom() {
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
		create();
		throwIfNotAllowed();
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
		annotadedCustoms();
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

	public void setActionKey(ActionKey actionKey) {
		this.actionKey = actionKey;
	}

	protected boolean requestHasObject() {
		return object != null || (objects != null && objects.size() > 0);
	}

	protected boolean isArray() {
		return objects != null;
	}

	protected boolean isAction(HttpVerb verb, String actionName) {
		return isAction(verb, actionName, false);
	}

	protected boolean isActionOverCollection(HttpVerb verb, String actionName) {
		return isAction(verb, actionName, true);
	}

	protected boolean isAction(HttpVerb verb, String actionName, boolean overCollection) {
		if (actionKey == null) {
			return false;
		}
		return actionKey.equals(new ActionKey(verb, actionName, overCollection));
	}

	private void annotadedCustoms() {
		Method[] methods = getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (!methodIsForAction(method)) {
				continue;
			}
			protectCustomAction(method);
		}
	}

	private void protectCustomAction(Method method) {
		try {
			method.invoke(this);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean methodIsForAction(Method method) {
		return actionKey.getVerb().hasAnnotation(method) && sameActionName(method);
	}

	private boolean sameActionName(Method method) {
		return actionKey.getVerb().getAnnotationValue(method).equals(actionKey.getActionName());
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
