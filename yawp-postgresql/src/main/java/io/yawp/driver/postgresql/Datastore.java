package io.yawp.driver.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

public class Datastore {

	public Key put(Entity entity) {
		Connection connection = ConnectionPool.connection();

		String sql = String.format("insert into %s (entity) values (?)", entity.getKey().getKind());

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setObject(1, createJsonObject(entity.serialize()));

			ps.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return entity.getKey();
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

	private PGobject createJsonObject(String json) throws SQLException {
		System.out.println("json: " + json);
		PGobject jsonObject = new PGobject();
		jsonObject.setType("jsonb");
		jsonObject.setValue(json);
		return jsonObject;
	}
}
