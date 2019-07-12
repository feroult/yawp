package io.yawp.repository.query;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DatastoreQueryForcedResponseTest extends EndpointTestCase {

	@Test
	public void testForcedResponseQueryViaList() {
		BasicObject xpto = yawp.save(new BasicObject("xpto"));

		List<BasicObject> nonForcedList = yawp(BasicObject.class).list();
		assertEquals(1, nonForcedList.size());

		List<BasicObject> forcedList = yawp(BasicObject.class)
				.forceResult(QueryType.QUERY, Arrays.asList(xpto.getId(), xpto.getId()))
				.list();
		assertEquals(2, forcedList.size());
	}

	@Test
	public void testForcedResponseQueryViaOnly() {
		yawp.save(new BasicObject("xpto"));
		BasicObject fake = yawp.save(new BasicObject("fake"));

		BasicObject nonForcedObj = yawp(BasicObject.class)
				.where("stringValue", "=", "xpto")
				.only();
		assertEquals("xpto", nonForcedObj.getStringValue());

		BasicObject forcedObj = yawp(BasicObject.class)
				.where("stringValue", "=", "xpto")
				.forceResult(QueryType.QUERY, Arrays.asList(fake.getId()))
				.only();
		assertEquals("fake", forcedObj.getStringValue());
	}

	@Test
	public void testForcedResponseQueryViaFirst() {
		yawp.save(new BasicObject("xpto"));
		BasicObject fake = yawp.save(new BasicObject("fake"));

		BasicObject nonForcedObj = yawp(BasicObject.class)
				.where("stringValue", "=", "xpto")
				.first();
		assertEquals("xpto", nonForcedObj.getStringValue());

		BasicObject forcedObj = yawp(BasicObject.class)
				.where("stringValue", "=", "xpto")
				.forceResult(QueryType.QUERY, Arrays.asList(fake.getId()))
				.first();
		assertEquals("fake", forcedObj.getStringValue());
	}

	@Test
	public void testForcedResponseQueryViaIds() {
		BasicObject xpto = yawp.save(new BasicObject("xpto"));

		List<IdRef<BasicObject>> nonForcedList = yawp(BasicObject.class).ids();
		assertEquals(1, nonForcedList.size());

		List<IdRef<BasicObject>> forcedList = yawp(BasicObject.class).forceResult(QueryType.QUERY, Arrays.asList(xpto.getId(), xpto.getId())).ids();
		assertEquals(2, forcedList.size());
	}

	@Test
	public void testForcedResponseQueryViaOnlyId() {
		BasicObject xpto = yawp.save(new BasicObject("xpto"));
		BasicObject fake = new BasicObject("fake");
		fake.setId(IdRef.create(yawp, BasicObject.class, "oni"));

		IdRef<BasicObject> nonForcedId = yawp(BasicObject.class).onlyId();
		assertEquals(xpto.getId(), nonForcedId);

		IdRef<BasicObject> forcedId = yawp(BasicObject.class).forceResult(QueryType.QUERY, Arrays.asList(fake.getId())).onlyId();
		assertEquals("/basic_objects/oni", forcedId.toString());
	}

	@Test
	public void testForcedResponseById() {
		BasicObject xpto = yawp.save(new BasicObject("xpto"));
		BasicObject fake = new BasicObject("fake");

		BasicObject nonForcedObj = yawp.query(BasicObject.class).fetch(xpto.getId());
		assertEquals("xpto", nonForcedObj.getStringValue());

		BasicObject forcedObj = yawp(BasicObject.class).forceResult(QueryType.FETCH, fake).fetch(xpto.getId());
		assertEquals("fake", forcedObj.getStringValue());
	}

	@Test
	public void testClearForcedResponse() {
		QueryBuilder<BasicObject> q = yawp(BasicObject.class).forceResult(QueryType.QUERY, Collections.<IdRef<BasicObject>>emptyList()).clearForcedResults();
		assertNull(q.getForcedResult(QueryType.QUERY));
	}
}
