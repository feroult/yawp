package io.yawp.servlet.rest;

import io.yawp.commons.http.HttpException;

public class DestroyRestAction extends RestAction {

	public DestroyRestAction() {
		super("destroy");
	}

	@Override
	public void shield() {
		shield.protectDestroy();
	}

	@Override
	public Object action() {
		if (overCollection()) {
			throw new HttpException(501, "DESTROY is not implemented for collections");
		}

		id.delete();
		return id;
	}

	private boolean overCollection() {
		return id == null;
	}

}
