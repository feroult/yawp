package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.servlet.HttpException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public abstract class ShieldBase<T> extends Feature {

	private boolean allow = false;

	private boolean lastAllow = false;

	private BaseCondition condition = null;

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

	protected final ShieldBase<T> allow(boolean allow) {
		this.allow = this.allow || allow;
		this.lastAllow = allow;
		return this;
	}

	protected final ShieldBase<T> where(BaseCondition condition) {
		if (!lastAllow) {
			return this;
		}
		this.condition = condition;
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
		throwNotFoundIfNotAllowed();
	}

	public final void protectShow() {
		always();
		show(id);
		throwNotFoundIfNotAllowed();
	}

	public final void protectCreate() {
		always();
		create(object, objects);
		throwNotFoundIfNotAllowed();

		verifyConditionOnIncomingObjects();
		throwForbiddenIfNotAllowed();
	}

	public final void protectUpdate() {
		always();
		update(id, object);
		throwNotFoundIfNotAllowed();

		verifyConditionOnIncomingObjects();
		throwForbiddenIfNotAllowed();
	}

	public final void protectDestroy() {
		always();
		destroy(id);
		throwNotFoundIfNotAllowed();
	}

	public final void protectCustom() {
		always();
		custom();
		annotadedCustoms();
		throwNotFoundIfNotAllowed();
	}

	private void throwNotFoundIfNotAllowed() {
		if (!allow) {
			throw new HttpException(404);
		}
	}

	private void throwForbiddenIfNotAllowed() {
		if (!allow) {
			throw new HttpException(403);
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

	public BaseCondition getCondition() {
		return condition;
	}

	public boolean hasCondition() {
		return condition != null;
	}

	private void verifyConditionOnIncomingObjects() {
		if (!hasCondition()) {
			return;
		}
		this.allow = evaluateConditionOnIncomingObjects();
	}

	private boolean evaluateConditionOnIncomingObjects() {
		if (objects != null) {
			return evaluateConditionOnIncomingObjects(objects);
		}
		return condition.evaluate(object);
	}

	private boolean evaluateConditionOnIncomingObjects(List<T> objects) {
		boolean result = true;
		for (Object object : objects) {
			result = result && condition.evaluate(object);
			if (!result) {
				return false;
			}
		}
		return true;
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
