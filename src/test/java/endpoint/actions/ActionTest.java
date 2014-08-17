package endpoint.actions;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import endpoint.ChildWithIdRef;
import endpoint.ObjectWithIdRef;
import endpoint.SimpleObject;
import endpoint.response.HttpResponse;
import endpoint.utils.DateUtils;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class ActionTest extends EndpointTestCase {

	@Test
	public void testSimpleAction() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");
		r.save(object);

		HttpResponse response = r.action(SimpleObject.class, "PUT", "active", object.getId(), null);
		object = JsonUtils.from(r, response.getText(), SimpleObject.class);

		assertEquals("i was changed in action", object.getAString());

		object = r.query(SimpleObject.class).id(object.getId());
		assertEquals("i was changed in action", object.getAString());
	}

	@Test
	public void testActionWithParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("x", "xpto");

		HttpResponse response = r.action(SimpleObject.class, "PUT", "params_action", 1l, params);
		assertEquals("xpto", response.getText());
	}

	@Test
	public void testActionOverCollection() {
		HttpResponse response = r.action(SimpleObject.class, "GET", "me", null, null);
		assertEquals("xpto", response.getText());
	}

	@Test
	public void testObjectWithIdRefAction() {
		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		r.save(object);
		r.action(ObjectWithIdRef.class, "PUT", "upper", object.getId().asLong(), null);

		ObjectWithIdRef retrievedObject = object.getId().fetch();
		assertEquals("XPTO", retrievedObject.getText());
	}

	@Test
	public void testChildWithIdRefAction() {
		ObjectWithIdRef object = new ObjectWithIdRef("XPTO");
		r.save(object);

		ChildWithIdRef child = new ChildWithIdRef("CHILD XPTO");
		child.setObjectWithIdRefId(object.getId());
		r.save(child);

		r.action(ChildWithIdRef.class, "PUT", "lower", object.getId().asLong(), null);

		ObjectWithIdRef retrievedObject = object.getId().fetch();
		assertEquals("xpto", retrievedObject.getText());

		ChildWithIdRef retrievedChild = object.getId().fetch(ChildWithIdRef.class);
		assertEquals("child xpto", retrievedChild.getText());
	}
}
