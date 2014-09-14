package endpoint.repository.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class DeleteAllTest extends EndpointTestCase {
	private Helper helper;

	@Before
	public void before() {
		helper = new Helper();
	}

	@Test
	public void deleteAll() {
		helper.createEntityWithIndexedProperty("person", "name", "john");
		helper.createEntityWithIndexedProperty("car", "model", "ferrari");
		helper.createEntityWithIndexedProperty("band", "style", "blues");

		assertNotNull(helper.queryEntity("person", "name", "john"));
		assertNotNull(helper.queryEntity("car", "model", "ferrari"));
		assertNotNull(helper.queryEntity("band", "style", "blues"));

		DeleteAll.now();

		assertNull(helper.queryEntity("person", "name", "john"));
		assertNull(helper.queryEntity("car", "model", "ferrari"));
		assertNull(helper.queryEntity("band", "style", "blues"));
	}

}
