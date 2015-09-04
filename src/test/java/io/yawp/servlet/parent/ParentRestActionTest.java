package io.yawp.servlet.parent;

import static org.junit.Assert.assertEquals;
import io.yawp.repository.models.parents.Job;
import io.yawp.repository.models.parents.Parent;

import java.util.List;

import org.junit.Test;

public class ParentRestActionTest extends ParentServletTestCase {

	@Test
	public void testCreate() {
		String json = post("/parents", "{ name: 'xpto' } ");
		Parent object = from(json, Parent.class);

		assertEquals("xpto", object.getName());
	}

	@Test
	public void testCreateArray() {
		String json = post("/parents", "[ { name: 'xpto1' }, { name: 'xpto2' } ]");
		List<Parent> parents = fromList(json, Parent.class);

		assertEquals(2, parents.size());
		assertEquals("xpto1", parents.get(0).getName());
		assertEquals("xpto2", parents.get(1).getName());
	}

	@Test
	public void testUpdate() {
		Parent parent = saveParent("xpto");

		String json = put(uri("/parents/%s", parent), "{ name: 'changed xpto' } ");
		Parent retrievedParent = from(json, Parent.class);

		assertEquals("changed xpto", retrievedParent.getName());
	}

	@Test
	public void testPatch() {
		Job job = new Job("coder");
		yawp.save(job);

		Parent parent = saveParent("xpto");
		parent.setJobId(job.getId());
		yawp.save(parent);

		String json = patch(uri("/parents/%s", parent), "{ name: 'xpto' } ");
		Parent retrievedParent = from(json, Parent.class);

		assertEquals("xpto", retrievedParent.getName());
		assertEquals("coder", retrievedParent.getJobId().fetch().getName());
	}

	@Test
	public void testShow() {
		Parent parent = saveParent("xpto");

		String json = get(uri("/parents/%s", parent));
		Parent retrievedParent = from(json, Parent.class);

		assertEquals("xpto", retrievedParent.getName());
	}

	@Test
	public void testIndex() {
		saveParent("xpto1");
		saveParent("xpto2");

		String json = get("/parents");
		List<Parent> parents = fromList(json, Parent.class);

		assertEquals(2, parents.size());
		assertEquals("xpto1", parents.get(0).getName());
		assertEquals("xpto2", parents.get(1).getName());
	}

	@Test
	public void testDestroy() {
		Parent parent = saveParent("xpto1");

		delete(uri("/parents/%s", parent));
		assertGetWithStatus(uri("/parents/%s", parent), 404);
	}

	@Test
	public void testCreateAndShowWithIdAsLong() {
		put("/parents/1", "{ name: 'xpto' } ");
		String json = get("/parents/1");

		Parent object = from(json, Parent.class);
		assertEquals("xpto", object.getName());
	}

	@Test
	public void testCreateAndShowWithIdAsString() {
		put("/parents/a", "{ name: 'xpto' } ");
		String json = get("/parents/a");

		Parent object = from(json, Parent.class);
		assertEquals("xpto", object.getName());
	}

	@Test
	public void testEndpointPathNotFound() {
		assertGetWithStatus("/parentz", 404);
		assertGetWithStatus("/parentz/123", 404);
		assertGetWithStatus("/parents/123/childz", 404);
	}
}
