package endpoint.hooks;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import endpoint.DatastoreObject;
import endpoint.Repository;
import endpoint.SimpleObject;
import endpoint.utils.DateUtils;
import endpoint.utils.GAETest;

public class HookTest extends GAETest {

	public class Product extends DatastoreObject {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private Repository r;

	@Before
	public void before() {
		r = new Repository();
	}

	@Test
	public void testAfterSave() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");
		r.save(object);

		assertEquals("just rock it", object.getChangeInCallback());
	}

	@Test
	public void testGenericCallback() {
		Product product = new Product();
		product.setName("xpto");

		r.save(product);

		assertEquals("xpto GenericHook touch", product.getName());
	}

}
