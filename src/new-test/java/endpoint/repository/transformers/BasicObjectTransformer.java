package endpoint.repository.transformers;

import java.util.HashMap;
import java.util.Map;

import endpoint.repository.models.basic.BasicObject;

public class BasicObjectTransformer extends Transformer<BasicObject> {

	public Map<String, Object> simple(BasicObject object) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("innerValue", object.getStringValue());
		map.put("innerObject", object);

		return map;
	}

}
