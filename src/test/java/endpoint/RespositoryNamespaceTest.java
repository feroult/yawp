package endpoint;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class RespositoryNamespaceTest extends EndpointTestCase {

	private Repository r1;
	private Repository r2;

	@Before
	public void before() {
		r1 = Repository.r("ns1");
		r2 = Repository.r("ns2");
	}

	@Test
	public void testFindById() {
		SimpleObject object1 = new SimpleObject("xpto1");
		r1.save(object1);

		assertNotNull(r1.query(SimpleObject.class).id(object1.getId()));
		assertNull(r2.query(SimpleObject.class).id(object1.getId()));
	}

	@Test
	public void testQuery() {
		r2.save(new SimpleObject("xpto2"));

		assertNotNull(r2.query(SimpleObject.class).where("aString", "=", "xpto2").first());
		assertNull(r1.query(SimpleObject.class).where("aString", "=", "xpto2").first());
	}

	@Test
	public void testSaveAndChange() {
		SimpleObject object1 = new SimpleObject("xpto");
		SimpleObject object2 = new SimpleObject("xpto");

		r1.save(object1);
		r2.save(object2);

		assertNotNull(r1.query(SimpleObject.class).id(object1.getId()));
		assertNotNull(r2.query(SimpleObject.class).id(object2.getId()));

		object1.setaString("lala");
		r1.save(object1);

		assertNull(r1.query(SimpleObject.class).where("aString", "=", "xpto").first());
		assertNotNull(r2.query(SimpleObject.class).where("aString", "=", "xpto").first());
	}
}
