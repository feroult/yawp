package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;

import java.sql.PreparedStatement;
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
		String sql = String.format("select key, properties from :kind where properties->>'name' = ?", kind);
		return new DatastoreSqlRunner(sql, kind) {

			@Override
			protected void prepare(PreparedStatement ps) throws SQLException {
				ps.setString(1, "jim");
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
