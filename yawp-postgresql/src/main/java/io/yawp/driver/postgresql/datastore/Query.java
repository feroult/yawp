package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		String sql = "select key, properties from :kind where properties->>'name' = :name";
		return new DatastoreSqlRunner(kind, sql) {

			@Override
			protected void bind() {
				bind("name", "jim");
			}

			@Override
			protected Object collect(ResultSet rs) throws SQLException {
				List<Entity> entities = new ArrayList<Entity>();

				while (rs.next()) {
					entities.add(getEntity(rs));
				}

				return entities;
			}

		};
	}
}
