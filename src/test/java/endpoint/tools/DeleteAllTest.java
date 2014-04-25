package endpoint.tools;

import static org.junit.Assert.assertNotNull;

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
		helper.createEntityWithProperty("person", "name", "john");
		helper.createEntityWithProperty("car", "model", "ferrari");
		helper.createEntityWithProperty("band", "style", "blues");

		DeleteAll.now();
		
		assertNotNull(helper.queryEntity("person", "name", "john"));
		assertNotNull(helper.queryEntity("car", "model", "ferrari"));
		assertNotNull(helper.queryEntity("band", "style", "blues"));
	}

}
