package io.yawp.repository.query.condition;

public enum LogicalOperator {
	AND {
		@Override
		public LogicalOperator not() {
			return OR;
		}
	},
	OR {
		@Override
		public LogicalOperator not() {
			return AND;
		}
	};

	public abstract LogicalOperator not();
}