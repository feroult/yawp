package endpoint.models;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import endpoint.models.DatastoreQueryOptions;

public class DatastoreQueryOptionsTest {

	@Test
	public void testEmpty() {
		String q = "{}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		assertNull(options.getWhere());
		assertNull(options.getOrder());
		assertNull(options.getLimit());
	}

	@Test
	public void testQueryOptions() {
		String q = "{where: ['aLong', '=', 1, 'aInt', '=', 3, 'aDouble', '=', 4.3], order: ['aString', 'desc'], limit: 2}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		assertArrayEquals(new Object[] { "aLong", "=", 1l, "aInt", "=", 3l, "aDouble", "=", 4.3 }, options.getWhere());
		assertArrayEquals(new String[] { "aString", "desc" }, options.getOrder());
		assertEquals(new Integer(2), options.getLimit());
	}
}
