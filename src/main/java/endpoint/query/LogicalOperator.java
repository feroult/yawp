package endpoint.query;

import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

public enum LogicalOperator {
	AND {
		public Filter join(Class<?> clazz, Condition... conditions) {
			return CompositeFilterOperator.and(convert(clazz, conditions));
		}
	}, OR {
		public Filter join(Class<?> clazz, Condition... conditions) {
			return CompositeFilterOperator.or(convert(clazz, conditions));
		}		
	};

	public abstract Filter join(Class<?> clazz, Condition... conditions);
	
	public static Filter[] convert(Class<?> clazz, Condition... conditions) {
		Filter[] filters = new Filter[conditions.length];
		for (int i = 0; i < conditions.length; i++) {
			filters[i] = conditions[i].getPredicate(clazz);
		}
		return filters;
	}
}