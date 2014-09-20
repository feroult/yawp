package io.yawp.servlet.rest;

import io.yawp.servlet.HttpException;

public class DestroyRestAction extends RestAction {

	@Override
	public Object action() {
		if (overCollection()) {
			throw new HttpException(501, "DESTROY is not implemented for collections");
		}

		Object object = id.fetch();
		id.delete();
		return object;
	}

	private boolean overCollection() {
		return id == null;
	}

}
