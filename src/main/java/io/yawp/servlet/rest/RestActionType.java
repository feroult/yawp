package io.yawp.servlet.rest;

import io.yawp.repository.annotations.Endpoint;
import io.yawp.servlet.HttpException;
import io.yawp.utils.HttpVerb;

public enum RestActionType {

	INDEX {

		@Override
		public Class<? extends RestAction> getRestActionClazz() {
			return IndexRestAction.class;
		}

	},
	SHOW {

		@Override
		public Class<? extends RestAction> getRestActionClazz() {
			return ShowRestAction.class;
		}

	},
	CREATE {

		@Override
		public Class<? extends RestAction> getRestActionClazz() {
			return CreateRestAction.class;
		}

	},
	UPDATE {

		@Override
		public Class<? extends RestAction> getRestActionClazz() {
			return UpdateRestAction.class;
		}

	},
	DESTROY {

		@Override
		public Class<? extends RestAction> getRestActionClazz() {
			return DestroyRestAction.class;
		}
		
	}, CUSTOM;

	public static RestActionType defaultRestActionType(HttpVerb verb, boolean overCollection) {
		switch (verb) {
		case GET:
			return overCollection ? INDEX : SHOW;
		case POST:
			return CREATE;
		case PUT:
		case PATCH:
			assertNotOverCollection(overCollection);
			return UPDATE;
		case DELETE:
			assertNotOverCollection(overCollection);
			return DESTROY;
		}
		throw new HttpException(501, "Unsuported http verb " + verb);
	}

	private static void assertNotOverCollection(boolean overCollection) {
		if (overCollection) {
			throw new HttpException(501);
		}
	}

	public void validateRetrictions(Endpoint endpointAnnotation) {
		if (this == INDEX && !endpointAnnotation.index()) {
			throw new HttpException(403);
		}

		if (this == UPDATE && !endpointAnnotation.update()) {
			throw new HttpException(403);
		}
	}

	public Class<? extends RestAction> getRestActionClazz() {
		return null;
	}
}
