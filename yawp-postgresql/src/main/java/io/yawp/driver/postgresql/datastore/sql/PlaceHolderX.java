package io.yawp.driver.postgresql.datastore.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaceHolderX {

	private List<Integer> indexes = new ArrayList<Integer>();

	private Object value;

	public PlaceHolderX(Object value) {
		this.value = value;
	}

	public void addIndex(int index) {
		indexes.add(index);
	}

	public void setValue(PreparedStatement ps) {
		try {
			for (int index : indexes) {
				ps.setObject(index, value);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
