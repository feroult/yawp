package endpoint.repository.actions;

import java.util.Map;

import endpoint.repository.IdRef;
import endpoint.repository.SimpleObject;
import endpoint.repository.actions.annotations.GET;
import endpoint.repository.actions.annotations.PUT;
import endpoint.repository.response.JsonResponse;
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
