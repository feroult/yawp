package io.yawp.servlet.rest;

import io.yawp.repository.query.DatastoreQuery;

public class ShowRestAction extends RestAction {

	@Override
	public Object action() {
		DatastoreQuery<?> query = query();

		if (params.containsKey(TRANSFORMER)) {
			return query.transform(params.get(TRANSFORMER)).id(id);
		}

		return query.id(id);
	}

}
