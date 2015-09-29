package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgresql.util.PGobject;

public class DatastoreSqlRunner extends SqlRunner {

	private Key key;

	private Entity entity;

	private List<PlaceHolder> placeHolders;

	private Map<String, PlaceHolderX> placeHoldersX = new HashMap<String, PlaceHolderX>();

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

	public DatastoreSqlRunner(String kind, String query) {
		init(query.replaceAll(":kind", kind));
	}

	private void init(String query) {
		bind();

		if (placeHoldersX.size() > 0) {
			this.sql = query;
			parsePlaceHoldersIndexes();
			removePlaceHolders();
			return;
		}

		this.placeHolders = PlaceHolder.parse(query);
		this.sql = PlaceHolderKey.replaceAll(query);
	}

	private void parsePlaceHoldersIndexes() {
		Pattern pattern = Pattern.compile("\\:\\w+");
		Matcher matcher = pattern.matcher(sql);

		int index = 1;

		while (matcher.find()) {
			String key = matcher.group();
			PlaceHolderX placeHolderX = placeHoldersX.get(key);
			placeHolderX.addIndex(index++);
		}

		return;
	}

	private void removePlaceHolders() {
		for (String placeHolderKey : placeHoldersX.keySet()) {
			sql = sql.replaceAll(placeHolderKey, "?");
		}
	}

	@Override
	protected void prepare(PreparedStatement ps) throws SQLException {

		if (placeHoldersX.size() > 0) {
			for (PlaceHolderX placeHolder : placeHoldersX.values()) {
				placeHolder.setValue(ps);
			}
			return;
		}

		for (PlaceHolder placeHolder : placeHolders) {
			placeHolder.setValue(ps, key, entity);
		}
	}

	public void bind() {
	}

	private void bind(String placeHolderKey, PlaceHolderX placeHolderObject) {
		placeHoldersX.put(":" + placeHolderKey, placeHolderObject);
	}

	protected void bind(String placeHolderKey, Key key) {
		PlaceHolderX placeHolderObject = new PlaceHolderX(createJsonObject(key.serialize()));
		bind(placeHolderKey, placeHolderObject);
	}

	protected void bind(String placeHolderKey, Entity entity) {
		PlaceHolderX placeHolderObject = new PlaceHolderX(createJsonObject(entity.serializeProperties()));
		bind(placeHolderKey, placeHolderObject);
	}

	protected Entity getEntity(ResultSet rs) throws SQLException {
		PGobject keyObject = (PGobject) rs.getObject(1);
		PGobject entityObject = (PGobject) rs.getObject(2);

		Entity entity = new Entity(Key.deserialize(keyObject.getValue()));
		entity.deserializeProperties(entityObject.getValue());

		return entity;
	}

	protected PGobject createJsonObject(String json) {
		try {
			PGobject jsonObject = new PGobject();
			jsonObject.setType("jsonb");
			jsonObject.setValue(json);
			return jsonObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
