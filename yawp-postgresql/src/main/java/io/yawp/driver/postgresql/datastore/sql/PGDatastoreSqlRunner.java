package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PGDatastoreSqlRunner extends SqlRunner {

	private Key key;

	private Entity entity;

	private List<PlaceHolder> placeHolders;

	public PGDatastoreSqlRunner(Connection connection, String query, Key key, Entity entity) {
		super(connection);

		this.key = key;
		this.entity = entity;
		this.placeHolders = PlaceHolder.parse(query);

		this.sql = PlaceHolderKey.replaceAll(query.replaceAll(":kind", key.getKind()));
	}

	public PGDatastoreSqlRunner(Connection connection, String query, Entity entity) {
		this(connection, query, entity.getKey(), entity);
	}

	public PGDatastoreSqlRunner(Connection connection, String query, Key key) {
		this(connection, query, key, null);
	}

	@Override
	protected void prepare(PreparedStatement ps) throws SQLException {
		for (PlaceHolder placeHolder : placeHolders) {
			placeHolder.setValue(ps, key, entity);
		}
	}

}
