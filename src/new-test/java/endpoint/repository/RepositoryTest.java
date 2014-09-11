package endpoint.repository;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import endpoint.repository.models.basic.DataObject;
import endpoint.repository.models.basic.JsonPojo;
import endpoint.repository.query.NoResultException;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class RepositoryTest extends EndpointTestCase {

	private static final String DATA_OBJECT_JSON = "{stringValue: 'xpto', intValue : 1, longValue : 1, doubleValue : 1.1, booleanValue : true, dateValue : '2013/12/26 23:55:01'}";

	@Test
	public void testSave() {
		DataObject object = JsonUtils.from(r, DATA_OBJECT_JSON, DataObject.class);

		r.save(object);

		DataObject retrievedObject = object.getId().fetch();
		retrievedObject.assertObject("xpto", 1, 1l, 1.1, true, "2013/12/26 23:55:01");
	}

	@Test
	public void testJsonProperty() {
		DataObject object = new DataObject();
		object.setJsonValue(new JsonPojo("xpto"));

		r.save(object);

		DataObject retrievedObject = object.getId().fetch();
		assertEquals("xpto", retrievedObject.getJsonValue().getStringValue());
	}

	@Test
	public void testJsonArrayProperty() {
		DataObject object = new DataObject();

		List<JsonPojo> list = new ArrayList<JsonPojo>();
		list.add(new JsonPojo("xpto1"));
		list.add(new JsonPojo("xpto2"));
		object.setJsonList(list);

		r.save(object);

		DataObject retrievedObject = object.getId().fetch();
		assertEquals("xpto1", retrievedObject.getJsonList().get(0).getStringValue());
		assertEquals("xpto2", retrievedObject.getJsonList().get(1).getStringValue());
	}

	@Test
	public void testJsonMapWithLongKeyAndObjectValue() {
		DataObject object = new DataObject();

		Map<Long, JsonPojo> map = new HashMap<Long, JsonPojo>();

		map.put(1l, new JsonPojo("xpto1"));
		map.put(2l, new JsonPojo("xpto2"));

		object.setJsonMap(map);

		r.save(object);

		DataObject retrievedObject = object.getId().fetch();
		assertEquals("xpto1", retrievedObject.getJsonMap().get(1l).getStringValue());
		assertEquals("xpto2", retrievedObject.getJsonMap().get(2l).getStringValue());
	}

	@Test(expected = NoResultException.class)
	public void testDelete() {
		DataObject object = new DataObject();

		r.save(object);
		r.delete(object.getId());

		r.query(DataObject.class).id(object.getId());
	}

}
