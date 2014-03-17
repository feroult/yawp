package endpoint.models.actions;

import endpoint.models.SimpleObject;
import endpoint.models.Target;
import endpoint.models.actions.Action;
import endpoint.models.actions.PUT;
import endpoint.servlet.JsonResponse;
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
