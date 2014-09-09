package endpoint.repository;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import endpoint.repository.models.Child;
import endpoint.repository.models.Grandchild;
import endpoint.repository.models.Job;
import endpoint.repository.models.Parent;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class IdRefTest extends EndpointTestCase {

	@Test
	public void testSave() {
		Parent parent = new Parent("xpto");
		r.save(parent);

		parent = parent.getId().fetch();

		assertEquals("xpto", parent.getName());
	}

	@Test
	public void testWithRelation() {
		Parent parent = saveParentWithJob();

		parent = parent.getId().fetch();
		Job job = parent.getJobId().fetch();

		assertEquals("haha", job.getName());
	}

	@Test
	public void testQuery() {
		Parent object = saveParentWithJob();

		object = r.query(Parent.class).where("id", "=", object.getId()).only();
		assertEquals("xpto", object.getName());

		object = r.query(Parent.class).where("jobId", "=", object.getJobId().asLong()).only();
		assertEquals("xpto", object.getName());

		object = r.query(Parent.class).where("jobId", "=", object.getJobId()).only();
		assertEquals("xpto", object.getName());

	}

	@Test
	public void testJsonConversion() {
		Parent parent = saveParentWithJob();

		String json = JsonUtils.to(parent);
		parent = JsonUtils.from(r, json, Parent.class);

		Job job = parent.getJobId().fetch();
		assertEquals("haha", job.getName());
	}

	@Test
	public void testWithJsonListField() {
		Parent parent = new Parent("xpto");
		r.save(parent);

		Job job1 = new Job("hehe");
		r.save(job1);

		Job job2 = new Job("hihi");
		r.save(job2);

		String json = String.format("{id: '/parents/%d', name: 'lala', pastJobIds: ['/jobs/%d', '/jobs/%d']}", parent.getId().asLong(),
				job1.getId().asLong(), job2.getId().asLong());

		parent = JsonUtils.from(r, json, Parent.class);

		assertEquals("lala", parent.getName());
		assertEquals("hehe", parent.getPastJobIds().get(0).fetch().getName());
		assertEquals("hihi", parent.getPastJobIds().get(1).fetch().getName());
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
		List<Parent> objects = r.query(Parent.class).where("id", "in", inList)
				.list();
		assertEquals(2, objects.size());
	}

	@Test
	public void testParseParentId() {
		IdRef<Parent> parentId = IdRef.parse(r, "/parents/1");

		assertEquals(Parent.class, parentId.getClazz());
		assertEquals((Long) 1l, parentId.asLong());
	}

	@Test
	public void testParseChildId() {
		IdRef<Child> childId = IdRef.parse(r, "/parents/1/children/2");
		IdRef<Parent> parentId = childId.getParentId();

		assertEquals(Parent.class, parentId.getClazz());
		assertEquals((Long) 1l, parentId.asLong());

		assertEquals(Child.class, childId.getClazz());
		assertEquals((Long) 2l, childId.asLong());
	}

	@Test
	public void testParseGrandchildId() {
		IdRef<Grandchild> grandchildId = IdRef.parse(r, "/parents/1/children/2/grandchildren/3");
		IdRef<Child> childId = grandchildId.getParentId();
		IdRef<Parent> parentId = childId.getParentId();

		assertEquals(Parent.class, parentId.getClazz());
		assertEquals((Long) 1l, parentId.asLong());

		assertEquals(Child.class, childId.getClazz());
		assertEquals((Long) 2l, childId.asLong());

		assertEquals(Grandchild.class, grandchildId.getClazz());
		assertEquals((Long) 3l, grandchildId.asLong());
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
