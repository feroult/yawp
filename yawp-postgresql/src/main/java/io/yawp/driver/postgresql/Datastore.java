package io.yawp.driver.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

public class Datastore {

	private static final String SQL_CREATE = "insert into :kind (entity) values (:entity)";

	private static final String SQL_UPDATE = "update :kind set entity = :entity where entity->'key'->>'name' = :key";

	private static final String SQL_GET = "select entity from :kind where entity->'key'->>'name' = :key";

	private static final String SQL_EXISTS = "select exists(select 1 from :kind where entity->'key'->>'name' = :key) as exists";

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
		PreparedStatement ps = prepareStatement(SQL_GET, key);

		try {
			ResultSet rs = ps.executeQuery();

			if (!rs.next()) {
				return null;
			}

			PGobject pgObject = (PGobject) rs.getObject(1);
			return Entity.deserialize(pgObject.getValue());

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(Object key) {
		// TODO Auto-generated method stub

	}

	private boolean existsEntityWithThisKey(Key key) {
		PreparedStatement ps = prepareStatement(SQL_EXISTS, key);

		try {
			ResultSet rs = ps.executeQuery();
			rs.next();

			return rs.getBoolean(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private PGobject createJsonObject(String json) throws SQLException {
		PGobject jsonObject = new PGobject();
		jsonObject.setType("jsonb");
		jsonObject.setValue(json);
		return jsonObject;
	}

	private void execute(String query, Entity entity) {
		PreparedStatement ps = prepareStatement(query, entity);

		try {
			ps.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private PreparedStatement prepareStatement(String query, Entity entity) {
		return prepareStatement(query, entity.getKey(), entity);
	}

	private PreparedStatement prepareStatement(String query, Key key) {
		return prepareStatement(query, key, null);
	}

	private PreparedStatement prepareStatement(String query, Key key, Entity entity) {
		Connection connection = ConnectionPool.connection();

		int keyIndex = query.indexOf(":key");
		int entityIndex = query.indexOf(":entity");

		String sql = query.replaceAll(":kind", key.getKind()).replaceAll(":key", "?").replaceAll(":entity", "?");

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			if (entityIndex != -1) {
				ps.setObject(keyIndex == -1 || entityIndex < keyIndex ? 1 : 2, createJsonObject(entity.serialize()));
			}

			if (keyIndex != -1) {
				ps.setString(entityIndex == -1 || keyIndex < entityIndex ? 1 : 2, key.getName());
			}

			return ps;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void generateKey(Entity entity) {
		Key key = entity.getKey();

		if (!key.isNew()) {
			return;
		}
		key.generate();
	}

	private boolean isNewEntity(Entity entity) {
		Key key = entity.getKey();
		return key.isNew() || !existsEntityWithThisKey(key);
	}

}
