package io.yawp.driver.postgresql.datastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
		PreparedStatement ps = prepareStatement(SQL_GET, key);

		try {
			ResultSet rs = ps.executeQuery();

			if (!rs.next()) {
				return null;
			}

			PGobject keyObject = (PGobject) rs.getObject(1);
			PGobject entityObject = (PGobject) rs.getObject(2);

			Entity entity = new Entity(Key.deserialize(keyObject.getValue()));
			entity.deserializeProperties(entityObject.getValue());

			return entity;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(Key key) {
		execute(SQL_DELETE, key);
	}

	private boolean isNewEntity(Entity entity) {
		Key key = entity.getKey();
		return key.isNew() || !existsEntityWithThisKey(key);
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

	private void execute(String query, Entity entity) {
		PreparedStatement ps = prepareStatement(query, entity);

		try {
			ps.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void execute(String query, Key key) {
		PreparedStatement ps = prepareStatement(query, key);

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

		List<PlaceHolder> placeHolders = PlaceHolder.parse(query);
		String sql = PlaceHolderKey.replaceAll(query.replaceAll(":kind", key.getKind()));

		try {

			PreparedStatement ps = connection.prepareStatement(sql);

			for (PlaceHolder placeHolder : placeHolders) {
				placeHolder.setValue(ps, key, entity);
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

}
