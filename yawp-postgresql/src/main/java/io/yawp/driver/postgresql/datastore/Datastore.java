package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.driver.postgresql.sql.SqlRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Datastore {

	private static final String SQL_CREATE = "insert into :kind (key, properties) values (:key, :properties)";

	private static final String SQL_UPDATE = "update :kind set properties = :properties where key @> :key";

	private static final String SQL_GET = "select key, properties from :kind where key @> :key";

	private static final String SQL_EXISTS = "select exists(select 1 from :kind where key @> :key) as exists";

	private static final String SQL_DELETE = "delete from :kind where key @> :key";

	public static Datastore create(ConnectionManager connectionManager) {
		return new Datastore(connectionManager);
	}

	private ConnectionManager connectionManager;

	private Datastore(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
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

	public Entity get(final Key key) throws EntityNotFoundException {
		SqlRunner runner = new DatastoreSqlRunner(key.getKind(), SQL_GET) {
			@Override
			public void bind() {
				bind("key", key);
			}

			@Override
			public Entity collectSingle(ResultSet rs) throws SQLException {
				return getEntity(rs);
			}
		};

		Entity entity = connectionManager.executeQuery(runner);

		if (entity == null) {
			throw new EntityNotFoundException();
		}
		return entity;
	}

	public void delete(Key key) {
		execute(SQL_DELETE, key);
	}

	public List<Entity> query(Query query) {
		return connectionManager.executeQuery(query.createRunner());
	}

	private boolean isNewEntity(Entity entity) {
		Key key = entity.getKey();
		return key.isNew() || !existsEntityWithThisKey(key);
	}

	private boolean existsEntityWithThisKey(final Key key) {
		SqlRunner runner = new DatastoreSqlRunner(key.getKind(), SQL_EXISTS) {
			@Override
			public void bind() {
				bind("key", key);
			}

			@Override
			protected Object collectSingle(ResultSet rs) throws SQLException {
				return rs.getBoolean(1);
			}
		};

		return connectionManager.executeQuery(runner);
	}

	private void execute(String query, final Entity entity) {
		SqlRunner runner = new DatastoreSqlRunner(entity.getKind(), query) {
			@Override
			public void bind() {
				bind("key", entity.getKey());
				bind("properties", entity);
			}
		};

		connectionManager.execute(runner);
	}

	private void execute(String query, final Key key) {
		SqlRunner runner = new DatastoreSqlRunner(key.getKind(), query) {
			@Override
			public void bind() {
				bind("key", key);
			}
		};

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
