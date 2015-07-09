package io.yawp.repository.actions;

import io.yawp.commons.utils.ThrownExceptionsUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RepositoryActions {

	public static Object execute(Repository r, Method method, IdRef<?> id, Map<String, String> params) {
		try {

			@SuppressWarnings("unchecked")
			Class<? extends Action<?>> actionClazz = (Class<? extends Action<?>>) method.getDeclaringClass();

			Action<?> actionInstance = actionClazz.newInstance();
			actionInstance.setRepository(r);

			Object[] arguments = ActionKey.getActionMethodParameters(method, id, params);
			return method.invoke(actionInstance, arguments);

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			throw ThrownExceptionsUtils.handle(e);
		}
	}

}
