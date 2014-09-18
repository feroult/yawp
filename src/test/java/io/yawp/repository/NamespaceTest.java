package io.yawp.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.utils.EndpointTestCase;

import org.junit.Before;
import org.junit.Test;

public class NamespaceTest extends EndpointTestCase {

	private Repository r1;
	private Repository r2;

	@Before
	public void before() {
		r1 = Repository.r("ns1").setFeatures(r.getFeatures());
		r2 = Repository.r("ns2").setFeatures(r.getFeatures());
	}

	@Test
	public void testQueryId() {
		BasicObject object = new BasicObject();
		r1.save(object);

		assertNotNull(r1.query(BasicObject.class).id(object.getId()));
		assertNull(r2.query(BasicObject.class).whereById("=", object.getId()).first());
	}

	@Test
	public void testQueryProperty() {
		r2.save(new BasicObject("xpto2"));

		assertNotNull(r2.query(BasicObject.class).where("stringValue", "=", "xpto2").first());
		assertNull(r1.query(BasicObject.class).where("stringValue", "=", "xpto2").first());
	}

	@Test
	public void testSaveAndChange() {
		BasicObject object1 = new BasicObject("xpto");
		BasicObject object2 = new BasicObject("xpto");

		r1.save(object1);
		r2.save(object2);

		assertNotNull(r1.query(BasicObject.class).id(object1.getId()));
		assertNotNull(r2.query(BasicObject.class).id(object2.getId()));

		object1.setStringValue("lala");
		r1.save(object1);

		assertNull(r1.query(BasicObject.class).where("stringValue", "=", "xpto").first());
		assertNotNull(r2.query(BasicObject.class).where("stringValue", "=", "xpto").first());
	}
}
