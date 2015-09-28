package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.datastore.Key;
import io.yawp.repository.query.QueryOrder;

public class Query {

	private String kind;

	public Query(String kind) {
		this.kind = kind;
	}

	public void setKeysOnly() {
		// TODO Auto-generated method stub

	}

	public void setAncestor(Key key) {
		// TODO Auto-generated method stub

	}

	public void addSort(String string, QueryOrder order) {
		// TODO Auto-generated method stub

	}

}
