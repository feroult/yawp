package io.yawp.commons.utils.kind;

import io.yawp.repository.annotations.Endpoint;

public class OldKindResolver implements KindResolver {

	@Override
	public String getKind(Class<?> clazz) {
		Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
		return endpoint.path();
	}

	@Override
	public String getPath(String kind) {
		return kind;
	}

}
