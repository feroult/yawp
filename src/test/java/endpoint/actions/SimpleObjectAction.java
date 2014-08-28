package endpoint.actions;

import java.util.Map;

import endpoint.IdRef;
import endpoint.SimpleObject;
import endpoint.actions.annotations.GET;
import endpoint.actions.annotations.PUT;
import endpoint.response.JsonResponse;
import endpoint.utils.JsonUtils;

public class SimpleObjectAction extends Action<SimpleObject> {

	@PUT("active")
	public JsonResponse activate(IdRef<SimpleObject> id) {
		SimpleObject object = r.query(SimpleObject.class).id(id);
		object.setAString("i was changed in action");
		r.save(object);
		return new JsonResponse(JsonUtils.to(object));
	}

	@PUT("params_action")
	public JsonResponse paramsAction(IdRef<SimpleObject> id, Map<String, String> params) {
		return new JsonResponse(params.get("x"));
	}

	@GET(value = "me", overCollection = true)
	public JsonResponse me() {
		return new JsonResponse("xpto");
	}

}
