package io.yawp.servlet.rest;

import io.yawp.repository.query.DatastoreQuery;

public class ShowRestAction extends RestAction {

	public ShowRestAction() {
		super("show");
	}

	@Override
	public void shield() {
		shield.protectShow();
	}

	@Override
	public Object action() {
		DatastoreQuery<?> query = query();

		if (hasTransformer()) {
			return query.transform(getTransformerName()).fetch(id);
		}

		if (hasShieldCondition()) {
			query.and(shield.getCondition());
		}

		return query.fetch(id);
	}

}
