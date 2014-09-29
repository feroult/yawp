package io.yawp.repository.actions;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.utils.ThrownExceptionsUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RepositoryActions {

	public static Object execute(Repository r, IdRef<?> id, Method action, Map<String, String> params) {
		try {

			@SuppressWarnings("unchecked")
			Class<? extends Action<?>> actionClazz = (Class<? extends Action<?>>) action.getDeclaringClass();

			Action<?> actionInstance = actionClazz.newInstance();
			actionInstance.setRepository(r);

			Object[] arguments = getActionArguments(action, params, id);
			return action.invoke(actionInstance, arguments);

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			throw ThrownExceptionsUtils.handle(e);
		}
	}

	private static Object[] getActionArguments(Method action, Map<String, String> params, IdRef<?> id) {
		if (action.getParameterTypes().length == 0) {
			return new Object[] {};
		}
		if (action.getParameterTypes().length == 2) {
			return new Object[] { id, params };
		}

		if (IdRef.class.equals(action.getParameterTypes()[0])) {
			return new Object[] { id };
		}

		return new Object[] { params };
	}
}
