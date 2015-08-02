package io.yawp.repository.actions;

import io.yawp.commons.utils.ThrownExceptionsUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RepositoryActions {

	public static Object execute(Repository r, Method method, IdRef<?> id, Map<String, String> params) {
		boolean rollback = false;

		if (isAtomic(method)) {
			if (isAtomicCrossEntities(method)) {
				r.beginX();
			} else {
				r.begin();
			}
		}

		try {

			return invokeActionMethod(r, method, id, params);

		} catch (Throwable t) {
			rollback = true;

			if (r.isTransationInProgress()) {
				r.rollback();
			}

			throw t;

		} finally {
			if (r.isTransationInProgress()) {
				if (rollback) {
					throw new RuntimeException(
							"Running on devserver or unit tests? To test cross-group, default_high_rep_job_policy_unapplied_job_pct must be > 0");
				}

				r.commit();
			}
		}
	}

	private static boolean isAtomicCrossEntities(Method method) {
		return method.getAnnotation(Atomic.class).cross();
	}

	private static boolean isAtomic(Method method) {
		return method.isAnnotationPresent(Atomic.class);
	}

	private static Object invokeActionMethod(Repository r, Method method, IdRef<?> id, Map<String, String> params) {
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
