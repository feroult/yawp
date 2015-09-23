package io.yawp.driver.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

public class Datastore {

	public Key put(Entity entity) {
		if (isNewEntity(entity)) {
			createEntity(entity);
		} else {
			updateEntity(entity);
		}
		return entity.getKey();
	}

	private void updateEntity(Entity entity) {
		Connection connection = ConnectionPool.connection();

		String sql = String.format("update %s set entity = ? where entity->'key'->>'name' = ?", entity.getKey().getKind());

		try {

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setObject(1, createJsonObject(entity.serialize()));
			ps.setString(2, entity.getKey().getName());
			ps.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createEntity(Entity entity) {
		Connection connection = ConnectionPool.connection();

		generateKey(entity);

		String sql = String.format("insert into %s (entity) values (?)", entity.getKey().getKind());

		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setObject(1, createJsonObject(entity.serialize()));
			ps.execute();

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

	public Entity get(Key key) {
		Connection connection = ConnectionPool.connection();

		String sql = String.format("select entity from %s where entity->'key'->>'name' = ?", key.getKind());

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, key.getName());

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

	private boolean isNewEntity(Entity entity) {
		Key key = entity.getKey();
		return key.isNew() || !existsEntityWithKey(key);
	}

	private boolean existsEntityWithKey(Key key) {
		Connection connection = ConnectionPool.connection();
		String sql = String.format("select exists(select 1 from %s where entity->'key'->>'name' = ?) as exists", key.getKind());

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, key.getName());

			ResultSet rs = ps.executeQuery();
			rs.next();

			return rs.getBoolean(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private PGobject createJsonObject(String json) throws SQLException {
		System.out.println("json: " + json);
		PGobject jsonObject = new PGobject();
		jsonObject.setType("jsonb");
		jsonObject.setValue(json);
		return jsonObject;
	}
}
