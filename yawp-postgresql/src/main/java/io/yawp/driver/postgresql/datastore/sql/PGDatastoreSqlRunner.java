package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PGDatastoreSqlRunner extends SqlRunner {

	private Key key;

	private Entity entity;

	private List<PlaceHolder> placeHolders;

	public PGDatastoreSqlRunner(String query, Key key, Entity entity) {
		this.key = key;
		this.entity = entity;
		this.placeHolders = PlaceHolder.parse(query);

		this.sql = PlaceHolderKey.replaceAll(query.replaceAll(":kind", key.getKind()));
	}

	public PGDatastoreSqlRunner(String query, Entity entity) {
		this(query, entity.getKey(), entity);
	}

	public PGDatastoreSqlRunner(String query, Key key) {
		this(query, key, null);
	}

	@Override
	protected void prepare(PreparedStatement ps) throws SQLException {
		for (PlaceHolder placeHolder : placeHolders) {
			placeHolder.setValue(ps, key, entity);
		}
	}

}
