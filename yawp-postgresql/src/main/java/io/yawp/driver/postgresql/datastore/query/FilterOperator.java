package io.yawp.driver.postgresql.datastore.query;

public enum FilterOperator {
	EQUAL {
		@Override
		public String getText() {
			return "=";
		}
	},
	GREATER_THAN {
		@Override
		public String getText() {
			return ">";
		}
	},
	GREATER_THAN_OR_EQUAL {
		@Override
		public String getText() {
			return ">=";
		}
	},
	IN {
		@Override
		public String getText() {
			return "in";
		}
	},
	LESS_THAN {
		@Override
		public String getText() {
			return "<";
		}
	},
	LESS_THAN_OR_EQUAL {
		@Override
		public String getText() {
			return "<=";
		}
	},
	NOT_EQUAL {
		@Override
		public String getText() {
			return "<>";
		}
	};

	public abstract String getText();
}
