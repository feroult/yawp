package io.yawp.driver.postgresql.datastore;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SearchKeyPlaceHolder extends PlaceHolder {

	@Override
	public void setValue(PreparedStatement ps, Key key, Entity entity) throws SQLException {
		ps.setString(index, key.getName());
	}

}
