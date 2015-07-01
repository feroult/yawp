package io.yawp.servlet.rest;

import io.yawp.repository.query.DatastoreQuery;

public class ShowRestAction extends RestAction {

	public ShowRestAction() {
		super("show");
	}

	@Override
	public Object action() {
		if(hasShield()) {
			shield.protectShow();
		}


		DatastoreQuery<?> query = query();

		if (hasTransformer()) {
			return query.transform(getTransformerName()).fetch(id);
		}

		return query.fetch(id);
	}

}
