package endpoint.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class ReIndexTest extends EndpointTestCase {

	private Helper helper;

	@Before
	public void before() {
		helper = new Helper();
	}

	@Test
	public void testReIndexOneProperty() {
		helper.createEntityWithProperty("person", "name", "john");
		helper.createEntityWithProperty("person", "name", "peter");

		assertNull(helper.queryEntity("person", "name", "john"));
		assertNull(helper.queryEntity("person", "name", "peter"));

		List<Long> ids = ReIndex.now("person", "name");

		assertEquals(2, ids.size());
		assertNotNull(helper.queryEntity("person", "name", "john"));
		assertNotNull(helper.queryEntity("person", "name", "peter"));
	}

	@Test
	public void testReIndexTwoProperties() {
		helper.createEntityWithProperty("person", "name", "john", "age", 10);

		assertNull(helper.queryEntity("person", "name", "john"));
		assertNull(helper.queryEntity("person", "age", 10));

		List<Long> ids = ReIndex.now("person", "name", "age");

		assertEquals(1, ids.size());
		assertNotNull(helper.queryEntity("person", "name", "john"));
		assertNotNull(helper.queryEntity("person", "age", 10));
	}

	@Test
	public void textReIndexUrlAPI() {
		helper.createEntityWithProperty("person", "name", "john", "age", 10);
		assertNull(helper.queryEntity("person", "name", "john"));
		assertNull(helper.queryEntity("person", "age", 10));

		List<Long> ids = ReIndex.parse("/person/name/age/").now();

		assertEquals(1, ids.size());
		assertNotNull(helper.queryEntity("person", "name", "john"));
		assertNotNull(helper.queryEntity("person", "age", 10));
	}

}
