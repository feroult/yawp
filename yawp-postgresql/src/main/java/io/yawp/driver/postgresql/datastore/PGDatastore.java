package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.datastore.sql.PGDatastoreSqlRunner;
import io.yawp.driver.postgresql.datastore.sql.SqlRunner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

public class PGDatastore {

	private static final String SQL_CREATE = "insert into :kind (key, properties) values (:key, :properties)";

	private static final String SQL_UPDATE = "update :kind set properties = :properties where key @> :search_key";

	private static final String SQL_GET = "select key, properties from :kind where key @> :search_key";

	private static final String SQL_EXISTS = "select exists(select 1 from :kind where key @> :search_key) as exists";

	private static final String SQL_DELETE = "delete from :kind where key @> :search_key";

	public static PGDatastore create() {
		return new PGDatastore();
	}

	public Key put(Entity entity) {
		if (isNewEntity(entity)) {
			createEntity(entity);
		} else {
			updateEntity(entity);
		}
		return entity.getKey();
	}

	private void createEntity(Entity entity) {
		generateKey(entity);
		execute(SQL_CREATE, entity);
	}

	private void updateEntity(Entity entity) {
		execute(SQL_UPDATE, entity);
	}

	public Entity get(Key key) {
		Connection connection = ConnectionPool.connection();

		SqlRunner runner = new PGDatastoreSqlRunner(connection, SQL_GET, key) {
			@Override
			public Entity collectSingle(ResultSet rs) throws SQLException {
				PGobject keyObject = (PGobject) rs.getObject(1);
				PGobject entityObject = (PGobject) rs.getObject(2);

				Entity entity = new Entity(Key.deserialize(keyObject.getValue()));
				entity.deserializeProperties(entityObject.getValue());

				return entity;
			}
		};

		return runner.executeQuery();
	}

	public void delete(Key key) {
		execute(SQL_DELETE, key);
	}

	private boolean isNewEntity(Entity entity) {
		Key key = entity.getKey();
		return key.isNew() || !existsEntityWithThisKey(key);
	}

	private boolean existsEntityWithThisKey(Key key) {
		Connection connection = ConnectionPool.connection();

		SqlRunner runner = new PGDatastoreSqlRunner(connection, SQL_EXISTS, key) {
			@Override
			protected Object collectSingle(ResultSet rs) throws SQLException {
				return rs.getBoolean(1);
			}
		};

		return runner.executeQuery();
	}

	private void execute(String query, Entity entity) {
		Connection connection = ConnectionPool.connection();
		SqlRunner runner = new PGDatastoreSqlRunner(connection, query, entity);
		runner.execute();
	}

	private void execute(String query, Key key) {
		Connection connection = ConnectionPool.connection();
		SqlRunner runner = new PGDatastoreSqlRunner(connection, query, key);
		runner.execute();
	}

	private void generateKey(Entity entity) {
		Key key = entity.getKey();

		if (!key.isNew()) {
			return;
		}

		key.generate();
	}

}
