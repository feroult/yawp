package endpoint.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import endpoint.repository.models.parents.Parent;
import endpoint.utils.EndpointTestCase;

public class NamespaceTest extends EndpointTestCase {

	private Repository r1;
	private Repository r2;

	@Before
	public void before() {
		r1 = Repository.r("ns1");
		r2 = Repository.r("ns2");
	}

	@Test
	public void testQueryId() {
		Parent parent = new Parent();
		r1.save(parent);

		assertNotNull(r1.query(Parent.class).id(parent.getId()));
		assertNull(r2.query(Parent.class).whereById("=", parent.getId()).first());
	}

	@Test
	public void testQueryProperty() {
		r2.save(new Parent("xpto2"));

		assertNotNull(r2.query(Parent.class).where("name", "=", "xpto2").first());
		assertNull(r1.query(Parent.class).where("name", "=", "xpto2").first());
	}

	@Test
	public void testSaveAndChange() {
		Parent parent1 = new Parent("xpto");
		Parent parent2 = new Parent("xpto");

		r1.save(parent1);
		r2.save(parent2);

		assertNotNull(r1.query(Parent.class).id(parent1.getId()));
		assertNotNull(r2.query(Parent.class).id(parent2.getId()));

		parent1.setName("lala");
		r1.save(parent1);

		assertNull(r1.query(Parent.class).where("name", "=", "xpto").first());
		assertNotNull(r2.query(Parent.class).where("name", "=", "xpto").first());
	}
}
