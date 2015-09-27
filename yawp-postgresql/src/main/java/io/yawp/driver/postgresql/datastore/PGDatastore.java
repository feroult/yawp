package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.connection.ConnectionManager;
import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.driver.postgresql.datastore.sql.PGDatastoreSqlRunner;

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

	private ConnectionManager connectionManager;

	private PGDatastore() {
		this.connectionManager = new ConnectionManager();
	}

	@Deprecated
	public void dispose() {
		connectionManager.dispose();
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
		SqlRunner runner = new PGDatastoreSqlRunner(null, SQL_GET, key) {
			@Override
			public Entity collectSingle(ResultSet rs) throws SQLException {
				PGobject keyObject = (PGobject) rs.getObject(1);
				PGobject entityObject = (PGobject) rs.getObject(2);

				Entity entity = new Entity(Key.deserialize(keyObject.getValue()));
				entity.deserializeProperties(entityObject.getValue());

				return entity;
			}
		};

		return connectionManager.executeQuery(runner);
	}

	public void delete(Key key) {
		execute(SQL_DELETE, key);
	}

	private boolean isNewEntity(Entity entity) {
		Key key = entity.getKey();
		return key.isNew() || !existsEntityWithThisKey(key);
	}

	private boolean existsEntityWithThisKey(Key key) {
		SqlRunner runner = new PGDatastoreSqlRunner(null, SQL_EXISTS, key) {
			@Override
			protected Object collectSingle(ResultSet rs) throws SQLException {
				return rs.getBoolean(1);
			}
		};

		return connectionManager.executeQuery(runner);
	}

	private void execute(String query, Entity entity) {
		SqlRunner runner = new PGDatastoreSqlRunner(null, query, entity);
		connectionManager.execute(runner);
	}

	private void execute(String query, Key key) {
		SqlRunner runner = new PGDatastoreSqlRunner(null, query, key);
		connectionManager.execute(runner);
	}

	private void generateKey(Entity entity) {
		Key key = entity.getKey();

		if (!key.isNew()) {
			return;
		}

		key.generate();
	}

}
