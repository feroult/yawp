package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

public class DatastoreSqlRunner extends SqlRunner {

	public DatastoreSqlRunner(String kind, String sql) {
		super(sql.replaceAll(":kind", kind));
	}

	private void bind(String placeHolderKey, PlaceHolder placeHolderObject) {
		placeHolders.put(":" + placeHolderKey, placeHolderObject);
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
