package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.connection.PlaceHolder;
import io.yawp.driver.postgresql.connection.SqlRunner;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

public class DatastoreSqlRunner extends SqlRunner {

	public DatastoreSqlRunner(String kind, String sql) {
		super(sql.replaceAll(":kind", kind));
	}

	protected void bind(String placeHolderKey, Key key) {
		PlaceHolder placeHolderObject = new PlaceHolder(createJsonObject(key.serialize()));
		bind(placeHolderKey, placeHolderObject);
	}

	protected void bind(String placeHolderKey, Entity entity) {
		PlaceHolder placeHolderObject = new PlaceHolder(createJsonObject(entity.serializeProperties()));
		bind(placeHolderKey, placeHolderObject);
	}

	protected Entity getEntity(ResultSet rs) throws SQLException {
		PGobject keyObject = (PGobject) rs.getObject(1);
		PGobject entityObject = (PGobject) rs.getObject(2);

		Entity entity = new Entity(Key.deserialize(keyObject.getValue()));
		entity.deserializeProperties(entityObject.getValue());

		return entity;
	}
}
