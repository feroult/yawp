package endpoint.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import endpoint.HttpException;
import endpoint.IdRef;
import endpoint.Repository;
import endpoint.response.HttpResponse;
import endpoint.response.JsonResponse;
import endpoint.utils.EntityUtils;
import endpoint.utils.JsonUtils;
import endpoint.utils.ThrownExceptionsUtils;

public class RepositoryActions {

	public static HttpResponse execute(Repository r, Method action, IdRef<?> id, Map<String, String> params) {
		Object ret = null;
		return new JsonResponse(JsonUtils.to(ret));
	}

	public static HttpResponse execute(Repository r, Class<?> objectClazz, String action, Long id, Map<String, String> params) {
		try {
			Method method = r.getEndpointFeatures(objectClazz).getAction(action);

			if (method == null) {
				throw new HttpException(404);
			}

			@SuppressWarnings("unchecked")
			Class<? extends Action<?>> actionClazz = (Class<? extends Action<?>>) method.getDeclaringClass();

			Action<?> actionInstance = actionClazz.newInstance();
			actionInstance.setRepository(r);

			Object ret;
			if (method.getParameterTypes().length == 0) {
				ret = method.invoke(actionInstance);
			} else {
				Object idObject = isIdRefAction(method) ? createIdRef(r, objectClazz, id) : id;

				if (method.getParameterTypes().length == 1) {
					ret = method.invoke(actionInstance, idObject);
				} else {
					ret = method.invoke(actionInstance, idObject, params);
				}
			}

			if (method.getReturnType().equals(Void.TYPE)) {
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

	private static IdRef<?> createIdRef(Repository r, Class<?> objectClazz, Long id) {
		return IdRef.create(r, EntityUtils.getIdFieldRefClazz(objectClazz), id);
	}

	private static boolean isIdRefAction(Method method) {
		return IdRef.class.isAssignableFrom(method.getParameterTypes()[0]);
	}
}
