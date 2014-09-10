package endpoint.repository.parents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import endpoint.repository.parents.models.Address;
import endpoint.servlet.HttpException;

public final class Assertions {

	private Assertions() { throw new RuntimeException("Should not be instanciated"); }

	public static void assertError(MyEndpointServlet servlet, String verb, String url, int status) {
		try {
			servlet.execute(verb, url, null, null);
		} catch (HttpException ex) {
			assertEquals(status, ex.getHttpStatus());
			return;
		}
		assertTrue(false);
	}

	public static void assertListEmpty(List<?> list) {
		assertEquals(0, list.size());
	}

	public static void assertListEquals(List<?> list, String... expected) {
		assertEquals(expected.length, list.size());

		List<String> values = new ArrayList<>(list.size());
		for (Object o : list) {
			values.add(o.toString());
		}
		Collections.sort(values);
		List<String> expectedList = Arrays.asList(expected);
		Collections.sort(expectedList);

		for (int i = 0; i < list.size(); i++) {
			assertEquals(expectedList.get(i), values.get(i));
		}
	}

	public static void assertParentIdEquals(List<Address> addresses, Long parentId) {
		for (Address address : addresses) {
			assertEquals(parentId, address.getOwner().asLong());
		}
	}
}