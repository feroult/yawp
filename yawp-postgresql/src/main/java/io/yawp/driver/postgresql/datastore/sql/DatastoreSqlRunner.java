package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.postgresql.util.PGobject;

public class DatastoreSqlRunner extends SqlRunner {

	private Key key;

	private Entity entity;

	private List<PlaceHolder> placeHolders;

	public DatastoreSqlRunner(String query, Key key, Entity entity) {
		this.key = key;
		this.entity = entity;

		init(query.replaceAll(":kind", key.getKind()));
	}

	public DatastoreSqlRunner(String query, Entity entity) {
		this(query, entity.getKey(), entity);
	}

	public DatastoreSqlRunner(String query, Key key) {
		this(query, key, null);
	}

	public DatastoreSqlRunner(String query, String kind) {
		init(query.replaceAll(":kind", kind));
	}

	private void init(String query) {
		this.placeHolders = PlaceHolder.parse(query);
		this.sql = PlaceHolderKey.replaceAll(query);
	}

	@Override
	protected void prepare(PreparedStatement ps) throws SQLException {
		for (PlaceHolder placeHolder : placeHolders) {
			placeHolder.setValue(ps, key, entity);
		}
	}

	protected Entity getEntity(ResultSet rs) throws SQLException {
		PGobject keyObject = (PGobject) rs.getObject(1);
		PGobject entityObject = (PGobject) rs.getObject(2);

		Entity entity = new Entity(Key.deserialize(keyObject.getValue()));
		entity.deserializeProperties(entityObject.getValue());

		return entity;
	}

}
