package endpoint.transformers;

import java.util.HashMap;
import java.util.Map;

import endpoint.SimpleObject;
import endpoint.Target;

@Target(SimpleObject.class)
public class SimpleObjectTransformer extends Transformer {

	public Map<String, Object> simple(SimpleObject object) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("innerValue", object.getAString());

		Map<String, Long> innerObject = new HashMap<String, Long>();
		innerObject.put("aLong", object.getALong());
		map.put("innerObject", innerObject);

		return map;
	}

}
