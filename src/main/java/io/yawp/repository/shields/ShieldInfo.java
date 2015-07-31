package io.yawp.repository.shields;

import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.InvalidActionMethodException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShieldInfo<T> {

	private Class<? extends Shield<? super T>> shieldClazz;

	private Map<ActionKey, Method> actionMethods;

	public ShieldInfo(Class<? extends Shield<? super T>> shieldClazz) {
		this.shieldClazz = shieldClazz;
		parseActionMethods();
	}

	public Class<? extends Shield<? super T>> getShieldClazz() {
		return shieldClazz;
	}

	public Map<ActionKey, Method> getActionMethods() {
		return actionMethods;
	}

	private void parseActionMethods() {
		this.actionMethods = new HashMap<ActionKey, Method>();

		Method[] methods = shieldClazz.getDeclaredMethods();
		for (Method method : methods) {
			List<ActionKey> actionKeys = parseActionKeys(method);

			for (ActionKey actionKey : actionKeys) {
				actionMethods.put(actionKey, method);
			}
		}
	}

	private List<ActionKey> parseActionKeys(Method method) {
		try {
			return ActionKey.parseMethod(method);
		} catch (InvalidActionMethodException e) {
			throw new RuntimeException("Invalid action method in shield: " + shieldClazz.getName() + "." + method.getName(), e);
		}
	}
}
