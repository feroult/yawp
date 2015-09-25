package io.yawp.driver.postgresql.datastore.sql;


public enum PlaceHolderKey {
	KEY {
		@Override
		public PlaceHolder getPlaceHolder() {
			return new KeyPlaceHolder();
		}
	},
	PROPERTIES {
		@Override
		public PlaceHolder getPlaceHolder() {
			return new PropertiesPlaceHolder();
		}
	},
	SEARCH_KEY {
		@Override
		public PlaceHolder getPlaceHolder() {
			return new SearchKeyPlaceHolder();
		}
	};

	public abstract PlaceHolder getPlaceHolder();

	public String getText() {
		return ":" + name().toLowerCase();
	}

	public static String replaceAll(String query) {
		String sql = query;
		for (PlaceHolderKey placeHolderKey : values()) {
			sql = sql.replaceAll(placeHolderKey.getText(), "?");
		}
		return sql;
	}
}
