package io.yawp.commons.utils.kind;

import io.yawp.repository.annotations.Endpoint;

public class DefaultKindResolver extends KindResolver {

	@Override
	public String getKind(Class<?> clazz) {
		Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
		if (endpoint.path() == null) {
			return clazz.getSimpleName();
		}
		return endpoint.path().substring(1);
	}

	@Override
	public String getPath(String kind) {
		return "/" + kind;
	}

}
