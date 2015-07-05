package io.yawp.repository;

import static io.yawp.commons.http.HttpVerb.GET;
import static io.yawp.commons.http.HttpVerb.PUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Job;
import io.yawp.repository.models.parents.Parent;
import io.yawp.utils.EndpointTestCase;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class IdRefAsStringTest extends EndpointTestCase {

	@Test
	public void testFetch() {
		Parent parent = saveParent("xpto");

		Parent retrievedParent = parent.getId().fetch();
		assertEquals("xpto", retrievedParent.getName());
	}

	@Test
	public void testFetchWithinRelation() {
		Parent parent = saveParentWithJob();

		Parent retrievedParent = parent.getId().fetch();
		Job job = retrievedParent.getJobId().fetch();

		assertEquals("haha", job.getName());
	}

	@Test
	public void testQueryWithIdRef() {
		Parent parent = saveParentWithJob();

		Parent retrievedParent1 = yawp(Parent.class).where("id", "=", parent.getId()).only();
		assertEquals("xpto", retrievedParent1.getName());

		Parent retrievedParent2 = yawp(Parent.class).where("jobId", "=", parent.getJobId()).only();
		assertEquals("xpto", retrievedParent2.getName());
	}

	@Test
	public void testFetchAfterJsonConversion() {
		Parent parent = saveParentWithJob();

		String json = JsonUtils.to(parent);
		Parent retrievedParent = JsonUtils.from(yawp, json, Parent.class);

		Job job = retrievedParent.getJobId().fetch();
		assertEquals("haha", job.getName());
	}

	@Test
	public void testFetchWithinJsonListField() {
		Parent parent = saveParent("xpto");

		Job job1 = saveJob("hehe");
		Job job2 = saveJob("hihi");

		String json = String.format("{id: '/parents/%s', name: 'lala', pastJobIds: ['/jobs/%s', '/jobs/%s']}", parent.getId()
				.getSimpleValue(), job1.getId().getSimpleValue(), job2.getId().getSimpleValue());

		Parent retrievedParent = JsonUtils.from(yawp, json, Parent.class);

		assertEquals("lala", retrievedParent.getName());
		assertEquals("hehe", retrievedParent.getPastJobIds().get(0).fetch().getName());
		assertEquals("hihi", retrievedParent.getPastJobIds().get(1).fetch().getName());
	}

	@Test
	public void testFetchChild() {
		Parent parent = saveParent("xpto");

		saveChild("child xpto", parent);

		Child retrievedChild = parent.getId().child(Child.class);
		assertEquals("child xpto", retrievedChild.getName());
	}

	@Test
	public void testInOperator() {
		Parent parent1 = saveParent("xpto1");
		Parent parent2 = saveParent("xpto2");
		saveParent("xpto3");

		List<IdRef<Parent>> inList = Arrays.asList(parent1.getId(), parent2.getId());
		List<Parent> objects = yawp(Parent.class).where("id", "in", inList).list();
		assertEquals(2, objects.size());
	}

	@Test
	public void testParseParentId() {
		IdRef<Parent> parentId = IdRef.parse(yawp, GET, "/parents/a");
		assertIdRef(parentId, Parent.class, "a");
	}

	@Test
	public void testParseChildId() {
		IdRef<Child> childId = IdRef.parse(yawp, GET, "/parents/a/children/b");
		IdRef<Parent> parentId = childId.getParentId();

		assertIdRef(parentId, Parent.class, "a");
		assertIdRef(childId, Child.class, "b");
	}

	@Test
	public void testParseGrandchildId() {
		IdRef<Grandchild> grandchildId = IdRef.parse(yawp, GET, "/parents/a/children/b/grandchildren/c");
		IdRef<Child> childId = grandchildId.getParentId();
		IdRef<Parent> parentId = childId.getParentId();

		assertIdRef(parentId, Parent.class, "a");
		assertIdRef(childId, Child.class, "b");
		assertIdRef(grandchildId, Grandchild.class, "c");
	}

	private void assertIdRef(IdRef<?> id, Class<?> clazz, String idAsString) {
		assertEquals(clazz, id.getClazz());
		assertEquals(idAsString, id.asString());
		assertNull(id.asLong());
	}

	@Test
	public void testParseEndingWithCollection() {
		assertNull(IdRef.parse(yawp, GET, "/parents"));

		assertIdRef(IdRef.parse(yawp, GET, "/parents/a/children"), Parent.class, "a");
		assertIdRef(IdRef.parse(yawp, GET, "/parents/a/children/b/grandchildren"), Child.class, "b");
	}

	@Test
	public void testParseEndingWithActionOverObject() {
		assertNull(IdRef.parse(yawp, PUT, "/parents/touched"));
		assertNull(IdRef.parse(yawp, GET, "/parents/something"));

		assertIdRef(IdRef.parse(yawp, PUT, "/parents/a/touched"), Parent.class, "a");
		assertIdRef(IdRef.parse(yawp, PUT, "/parents/a/children/b/touched"), Child.class, "b");
	}

	@Test
	public void testParseEndingWithActionOverCollection() {
		assertIdRef(IdRef.parse(yawp, PUT, "/parents/a/children/touched"), Parent.class, "a");
		assertIdRef(IdRef.parse(yawp, PUT, "/parents/a/children/b/grandchildren/touched"), Child.class, "b");
	}

	@Test
	public void testToString() {
		assertEquals("/parents/a", IdRef.parse(yawp, GET, "/parents/a").toString());
		assertEquals("/parents/a/children/b", IdRef.parse(yawp, GET, "/parents/a/children/b").toString());
		assertEquals("/parents/a/children/b/grandchildren/c", IdRef.parse(yawp, GET, "/parents/a/children/b/grandchildren/c").toString());
	}

	private Parent saveParent(String name) {
		Parent parent = new Parent(name);
		parent.setId(IdRef.create(yawp, Parent.class, name));
		yawp.save(parent);
		return parent;
	}

	private void saveChild(String name, Parent parent) {
		Child child = new Child(name);
		child.setId(parent.getId().createChildId(Child.class, name));
		child.setParentId(parent.getId());
		yawp.save(child);
	}

	private Parent saveParentWithJob() {
		Job job = saveJob("haha");

		Parent parent = saveParent("xpto");
		parent.setJobId(job.getId());

		yawp.save(parent);

		return parent;
	}

	private Job saveJob(String name) {
		Job job = new Job(name);
		job.setId(IdRef.create(yawp, Job.class, name));
		yawp.save(job);
		return job;
	}

}
