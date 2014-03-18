package endpoint.actions;

import endpoint.SimpleObject;
import endpoint.Target;
import endpoint.actions.Action;
import endpoint.actions.PUT;
import endpoint.response.JsonResponse;
import endpoint.utils.JsonUtils;

@Target(SimpleObject.class)
public class SimpleObjectAction extends Action {

	@PUT("active")
	public JsonResponse activate(Long id) {
		SimpleObject object = r.findById(id, SimpleObject.class);
		object.setaString("i was changed in action");
		r.save(object);
		return new JsonResponse(JsonUtils.to(object));
	}

}
