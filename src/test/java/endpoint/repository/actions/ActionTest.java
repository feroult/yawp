package endpoint.repository.actions;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import endpoint.repository.ChildWithIdRef;
import endpoint.repository.ObjectWithIdRef;
import endpoint.repository.SimpleObject;
import endpoint.repository.response.HttpResponse;
import endpoint.servlet.routing.HttpVerb;
import endpoint.servlet.routing.Route;
import endpoint.utils.DateUtils;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class ActionTest extends EndpointTestCase {

	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleAction() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");
		r.save(object);

		HttpResponse response = callAction(HttpVerb.PUT, "/simpleobjects/" + object.getId() + "/active", null);
		object = JsonUtils.from(r, response.getText(), SimpleObject.class);

		assertEquals("i was changed in action", object.getAString());

		object = r.query(SimpleObject.class).id(object.getId());
		assertEquals("i was changed in action", object.getAString());
	}

	private HttpResponse callAction(HttpVerb verb, String uri, Map<String, String> params) {
		Route route = Route.generateRouteFor(r.getFeatures(), verb, uri);
		HttpResponse response = r.action(route.getIdRef(r), route.getCustomAction(), params);
		return response;
	}

	@Test
	public void testActionWithParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("x", "xpto");

		HttpResponse response = callAction(HttpVerb.PUT, "/simpleobjects/1/params_action", params);
		assertEquals("xpto", response.getText());
	}

	@Test
	public void testActionOverCollection() {
		HttpResponse response = callAction(HttpVerb.GET, "/simpleobjects/me", null);
		assertEquals("xpto", response.getText());
	}

	@Test
	public void testObjectWithIdRefAction() {
		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		r.save(object);
		callAction(HttpVerb.PUT, "/objectWithIdRef/" + object.getId() + "/upper", null);

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

		callAction(HttpVerb.PUT, "/child/" + child.getObjectWithIdRefId() + "/lower", null);

		ObjectWithIdRef retrievedObject = object.getId().fetch();
		assertEquals("xpto", retrievedObject.getText());

		ChildWithIdRef retrievedChild = object.getId().fetch(ChildWithIdRef.class);
		assertEquals("child xpto", retrievedChild.getText());
	}
}
