package endpoint.transformers;

import java.util.HashMap;
import java.util.Map;

import endpoint.SimpleObject;
import endpoint.Target;

@Target(SimpleObject.class)
public class SimpleObjectTransformer extends Transformer {

	public Map<String, String> simple(SimpleObject object) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(object.getaString(), object.getaString());
		return map;
	}

}
