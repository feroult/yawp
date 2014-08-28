package endpoint.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import endpoint.IdRef;
import endpoint.Repository;
import endpoint.response.HttpResponse;
import endpoint.response.JsonResponse;
import endpoint.utils.JsonUtils;
import endpoint.utils.ThrownExceptionsUtils;

public class RepositoryActions {

	public static HttpResponse execute(Repository r, IdRef<?> id, Method action, Map<String, String> params) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Action<?>> actionClazz = (Class<? extends Action<?>>) action.getDeclaringClass();

			Action<?> actionInstance = actionClazz.newInstance();
			actionInstance.setRepository(r);

			Object[] allArguments = new Object[] { id, params };
			Object[] arguments = Arrays.copyOf(allArguments, action.getParameterTypes().length);

			Object ret = action.invoke(actionInstance, arguments);
			if (action.getReturnType().equals(Void.TYPE)) {
				return null;
			}
			if (HttpResponse.class.isInstance(ret)) {
				return (HttpResponse) ret;
			}

			return new JsonResponse(JsonUtils.to(ret));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			throw ThrownExceptionsUtils.handle(e);
		}
	}

}
