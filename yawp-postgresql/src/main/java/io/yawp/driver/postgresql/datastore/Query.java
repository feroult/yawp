package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Query {

	private String kind;

	private BaseCondition c;

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

	public void setFilter(BaseCondition c) {
		this.c = c;
	}

	public SqlRunner createRunner() {
		return new DatastoreSqlRunner(kind, createSql()) {
			@Override
			protected void bind() {
				bind("name", "jim");
			}

			@Override
			protected Object collect(ResultSet rs) throws SQLException {
				return getEntities(rs);
			}

		};
	}

	private String createSql() {
		return "select key, properties from :kind where " + where();
	}

	private String where() {
		return "properties->>'name' = :name";
	}
}
