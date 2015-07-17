package io.yawp.repository.shields;

import io.yawp.commons.utils.EntityUtils;
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

	private Class<?> endpointClazz;

	private IdRef<?> id;

	private T object;

	private List<T> objects;

	private List<T> objectsX;

	private Map<String, String> params;

	private ActionKey actionKey;

	private Map<ActionKey, Method> actionMethods;

	public abstract void always();

	public abstract void index(IdRef<?> parentId);

	public abstract void show(IdRef<T> id);

	public abstract void create(List<T> objects);

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
		return objectsX != null;
	}

	protected final boolean requestHasObjectArray() {
		return objects != null;
	}

	public final void protectIndex() {
		always();
		index(id);
		throwNotFoundIfNotAllowed();
	}

	@SuppressWarnings("unchecked")
	public final void protectShow() {
		always();
		show((IdRef<T>) id);
		throwNotFoundIfNotAllowed();
	}

	public final void protectCreate() {
		always();
		create(objectsX);
		throwNotFoundIfNotAllowed();

		verifyConditionOnIncomingObjects();
		throwForbiddenIfNotAllowed();

		verifyConditionOnExistingObjects();
		throwForbiddenIfNotAllowed();
	}

	@SuppressWarnings("unchecked")
	public final void protectUpdate() {
		always();
		update((IdRef<T>) id, objectsX == null ? null : objectsX.get(0));
		throwNotFoundIfNotAllowed();

		verifyConditionOnIncomingObjects();
		throwForbiddenIfNotAllowed();

		verifyConditionOnExistingObjects();
		throwForbiddenIfNotAllowed();
	}

	@SuppressWarnings("unchecked")
	public final void protectDestroy() {
		always();
		destroy((IdRef<T>) id);
		throwNotFoundIfNotAllowed();

		verifyConditionOnExistingObjects();
		throwForbiddenIfNotAllowed();
	}

	public final void protectCustom() {
		always();
		custom();
		annotadedCustoms();
		throwNotFoundIfNotAllowed();

		verifyConditionOnExistingObjects();
		throwForbiddenIfNotAllowed();
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

	public void setEndpointClazz(Class<?> endpointClazz) {
		this.endpointClazz = endpointClazz;
	}

	public final void setId(IdRef<?> id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public final void setObject(Object object) {
		this.object = (T) object;
	}

	@SuppressWarnings("unchecked")
	public final void setObjects(List<?> objects) {
		this.objects = (List<T>) objects;
	}

	@SuppressWarnings("unchecked")
	public final void setObjectsX(List<?> objects) {
		this.objectsX = (List<T>) objects;
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

	private void verifyConditionOnExistingObjects() {
		if (!hasCondition()) {
			return;
		}
		this.allow = evaluateConditionOnExistingObjects();
	}

	private boolean evaluateConditionOnExistingObjects() {
		if (objects != null) {
			return evaluateConditionOnExistingObjects(objects);
		}
		IdRef<?> idInObject = getIdInIncomingObject();
		if (idInObject == null) {
			if (id == null || isParentId()) {
				return true;
			}
			return evaluateExistingObject(id);
		}
		return evaluateExistingObject(idInObject);
	}

	private boolean isParentId() {
		return !id.getClazz().equals(endpointClazz);
	}

	private IdRef<?> getIdInIncomingObject() {
		if (object == null) {
			return null;
		}
		return EntityUtils.getIdRef(object);
	}

	private boolean evaluateConditionOnExistingObjects(List<T> objects) {
		boolean result = true;
		for (Object object : objects) {
			IdRef<?> id = EntityUtils.getIdRef(object);
			result = result && (id == null || evaluateExistingObject(id));
			if (!result) {
				return false;
			}
		}
		return true;
	}

	public boolean evaluateExistingObject(IdRef<?> id) {
		Object object = id.fetch();
		if (object == null) {
			return true;
		}
		return condition.evaluate(object);
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
