package io.yawp.servlet.rest;

import io.yawp.repository.query.DatastoreQuery;
import io.yawp.repository.query.DatastoreQueryOptions;

import java.util.List;

public class IndexRestAction extends RestAction {

	@Override
	public List<?> action() {
		DatastoreQuery<?> query = query();

		if (id != null) {
			query.from(id);
		}

		if (params.containsKey(QUERY_OPTIONS)) {
			query.options(DatastoreQueryOptions.parse(params.get(QUERY_OPTIONS)));
		}

		if (params.containsKey(TRANSFORMER)) {
			return query.transform(params.get(TRANSFORMER)).list();
		}

		return query.list();
	}

}
