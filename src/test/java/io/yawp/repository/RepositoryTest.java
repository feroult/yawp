package io.yawp.repository;

import static org.junit.Assert.assertEquals;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.Pojo;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.NoResultException;
import io.yawp.utils.EndpointTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class RepositoryTest extends EndpointTestCase {

	private static final String DATA_OBJECT_JSON = "{stringValue: 'xpto', textValue: 'text xpto', intValue : 1, longValue : 1, doubleValue : 1.1, booleanValue : true, dateValue : '2013/12/26 23:55:01'}";

	@Test
	public void testSaveAllDataProperties() {
		BasicObject object = JsonUtils.from(yawp, DATA_OBJECT_JSON, BasicObject.class);

		yawp.save(object);

		BasicObject retrievedObject = object.getId().fetch();
		retrievedObject.assertObject("xpto", "text xpto", 1, 1l, 1.1, true, "2013/12/26 23:55:01");
	}

	@Test
	public void testJsonProperty() {
		BasicObject object = new BasicObject();
		object.setJsonValue(new Pojo("xpto"));

		yawp.save(object);

		BasicObject retrievedObject = object.getId().fetch();
		assertEquals("xpto", retrievedObject.getJsonValue().getStringValue());
	}

	@Test
	public void testJsonArrayProperty() {
		BasicObject object = new BasicObject();

		List<Pojo> list = new ArrayList<Pojo>();
		list.add(new Pojo("xpto1"));
		list.add(new Pojo("xpto2"));
		object.setJsonList(list);

		yawp.save(object);

		BasicObject retrievedObject = object.getId().fetch();
		assertEquals("xpto1", retrievedObject.getJsonList().get(0).getStringValue());
		assertEquals("xpto2", retrievedObject.getJsonList().get(1).getStringValue());
	}

	@Test
	public void testJsonMapWithLongKeyAndObjectValue() {
		BasicObject object = new BasicObject();

		Map<Long, Pojo> map = new HashMap<Long, Pojo>();

		map.put(1l, new Pojo("xpto1"));
		map.put(2l, new Pojo("xpto2"));

		object.setJsonMap(map);

		yawp.save(object);

		BasicObject retrievedObject = object.getId().fetch();
		assertEquals("xpto1", retrievedObject.getJsonMap().get(1l).getStringValue());
		assertEquals("xpto2", retrievedObject.getJsonMap().get(2l).getStringValue());
	}

	@Test(expected = NoResultException.class)
	public void testDelete() {
		BasicObject object = new BasicObject();

		yawp.save(object);
		yawp.destroy(object.getId());

		yawp(BasicObject.class).fetch(object.getId());
	}

	@Test
	public void testSaveParent() {
		Parent parent = new Parent("xpto");

		yawp.save(parent);

		Parent retrievedParent = parent.getId().fetch();
		assertEquals("xpto", retrievedParent.getName());
	}

	@Test
	public void testSaveChild() {
		Parent parent = new Parent();
		yawp.save(parent);

		Child child = new Child("xpto");
		child.setParentId(parent.getId());
		yawp.save(child);

		Parent retrievedParent = parent.getId().fetch();
		Child retrievedChild = child.getId().fetch();

		assertEquals(retrievedChild.getParentId(), retrievedParent.getId());
		assertEquals("xpto", retrievedChild.getName());
	}

	@Test
	public void testSaveGrandchild() {
		Parent parent = new Parent();
		yawp.save(parent);

		Child child = new Child();
		child.setParentId(parent.getId());
		yawp.save(child);

		Grandchild grandchild = new Grandchild("xpto");
		grandchild.setChildId(child.getId());
		yawp.save(grandchild);

		Child retrievedChild = child.getId().fetch();
		Grandchild retrievedGrandchild = grandchild.getId().fetch();

		assertEquals(retrievedGrandchild.getChildId(), retrievedChild.getId());
		assertEquals("xpto", retrievedGrandchild.getName());
	}

}
