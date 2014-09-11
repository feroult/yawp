package endpoint.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;
import endpoint.repository.models.parents.Job;
import endpoint.repository.models.parents.Parent;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class IdRefTest extends EndpointTestCase {

	@Test
	public void testFetch() {
		Parent parent = new Parent("xpto");
		r.save(parent);

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

		Parent retrievedParent1 = r.query(Parent.class).where("id", "=", parent.getId()).only();
		assertEquals("xpto", retrievedParent1.getName());

		Parent retrievedParent2 = r.query(Parent.class).where("jobId", "=", parent.getJobId()).only();
		assertEquals("xpto", retrievedParent2.getName());
	}

	@Test
	public void testFetchAfterJsonConversion() {
		Parent parent = saveParentWithJob();

		String json = JsonUtils.to(parent);
		Parent retrievedParent = JsonUtils.from(r, json, Parent.class);

		Job job = retrievedParent.getJobId().fetch();
		assertEquals("haha", job.getName());
	}

	@Test
	public void testFetchWithinJsonListField() {
		Parent parent = new Parent("xpto");
		r.save(parent);

		Job job1 = new Job("hehe");
		r.save(job1);

		Job job2 = new Job("hihi");
		r.save(job2);

		String json = String.format("{id: '/parents/%d', name: 'lala', pastJobIds: ['/jobs/%d', '/jobs/%d']}", parent.getId().asLong(),
				job1.getId().asLong(), job2.getId().asLong());

		Parent retrievedParent = JsonUtils.from(r, json, Parent.class);

		assertEquals("lala", retrievedParent.getName());
		assertEquals("hehe", retrievedParent.getPastJobIds().get(0).fetch().getName());
		assertEquals("hihi", retrievedParent.getPastJobIds().get(1).fetch().getName());
	}

	@Test
	public void testParentWithChild() {
		Parent parent = new Parent("xpto");
		r.save(parent);

		Child child = new Child("child xpto");
		child.setParentId(parent.getId());
		r.save(child);

		Child retrievedChild = parent.getId().child(Child.class);
		assertEquals("child xpto", retrievedChild.getName());
	}

	@Test
	public void testInOperator() {
		Parent parent1 = new Parent("xpto1");
		r.save(parent1);

		Parent parent2 = new Parent("xpto2");
		r.save(parent2);

		Parent parent3 = new Parent("xpto3");
		r.save(parent3);

		List<Long> inList = Arrays.asList(parent1.getId().asLong(), parent2.getId().asLong());
		List<Parent> objects = r.query(Parent.class).where("id", "in", inList).list();
		assertEquals(2, objects.size());
	}

	@Test
	public void testParseParentId() {
		IdRef<Parent> parentId = IdRef.parse(r, "/parents/1");

		assertIdRef(parentId, Parent.class, 1l);
	}

	@Test
	public void testParseChildId() {
		IdRef<Child> childId = IdRef.parse(r, "/parents/1/children/2");
		IdRef<Parent> parentId = childId.getParentId();

		assertIdRef(parentId, Parent.class, 1l);
		assertIdRef(childId, Child.class, 2l);
	}

	@Test
	public void testParseGrandchildId() {
		IdRef<Grandchild> grandchildId = IdRef.parse(r, "/parents/1/children/2/grandchildren/3");
		IdRef<Child> childId = grandchildId.getParentId();
		IdRef<Parent> parentId = childId.getParentId();

		assertIdRef(parentId, Parent.class, 1l);
		assertIdRef(childId, Child.class, 2l);
		assertIdRef(grandchildId, Grandchild.class, 3l);
	}

	private void assertIdRef(IdRef<?> id, Class<?> clazz, Long idAsLong) {
		assertEquals(clazz, id.getClazz());
		assertEquals(idAsLong, id.asLong());
	}

	@Test
	public void testParseUriWithCollectionOrAction() {
		assertNull(IdRef.parse(r, "/parents"));

		assertIdRef(IdRef.parse(r, "/parents/1/children"), Parent.class, 1l);
		assertIdRef(IdRef.parse(r, "/parents/1/action"), Parent.class, 1l);
		assertIdRef(IdRef.parse(r, "/parents/1/children/2/grandchildren"), Child.class, 2l);
		assertIdRef(IdRef.parse(r, "/parents/1/children/2/action"), Child.class, 2l);
	}

	@Test
	public void testParseUriWithActionOverCollection() {
		assertNull(IdRef.parse(r, "/parents"));

		assertIdRef(IdRef.parse(r, "/parents/1/children/action"), Parent.class, 1l);
		assertIdRef(IdRef.parse(r, "/parents/1/children/2/grandchildren/action"), Child.class, 2l);
	}

	@Test
	public void testToString() {
		assertEquals("/parents/1", IdRef.parse(r, "/parents/1").toString());
		assertEquals("/parents/1/children/2", IdRef.parse(r, "/parents/1/children/2").toString());
		assertEquals("/parents/1/children/2/grandchildren/3", IdRef.parse(r, "/parents/1/children/2/grandchildren/3").toString());
	}

	private Parent saveParentWithJob() {
		Job job = new Job("haha");
		r.save(job);

		Parent parent = new Parent("xpto");
		parent.setJobId(IdRef.create(r, Job.class, job.getId().asLong()));

		r.save(parent);

		return parent;
	}

}
