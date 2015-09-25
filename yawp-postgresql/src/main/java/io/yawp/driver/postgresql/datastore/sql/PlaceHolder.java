package io.yawp.driver.postgresql.datastore.sql;

import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.postgresql.util.PGobject;

public abstract class PlaceHolder {

	protected int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public abstract void setValue(PreparedStatement ps, Key key, Entity entity) throws SQLException;

	public static List<PlaceHolder> parse(String query) {
		List<PlaceHolder> placeHolders = new ArrayList<PlaceHolder>();

		for (PlaceHolderKey placeHolderKey : PlaceHolderKey.values()) {
			int index = query.indexOf(placeHolderKey.getText());
			if (index == -1) {
				continue;
			}

			PlaceHolder placeHolder = placeHolderKey.getPlaceHolder();
			placeHolder.setIndex(index);
			placeHolders.add(placeHolder);
		}

		sortIndexes(placeHolders);

		return placeHolders;
	}

	private static void sortIndexes(List<PlaceHolder> placeHolders) {
		Collections.sort(placeHolders, new Comparator<PlaceHolder>() {
			@Override
			public int compare(PlaceHolder p1, PlaceHolder p2) {
				return p1.getIndex() - p2.getIndex();
			}
		});

		int index = 1;
		for (PlaceHolder placeHolder : placeHolders) {
			placeHolder.setIndex(index++);
		}
	}

	protected PGobject createJsonObject(String json) throws SQLException {
		PGobject jsonObject = new PGobject();
		jsonObject.setType("jsonb");
		jsonObject.setValue(json);
		return jsonObject;
	}

}
