package endpoint.transformers;

import java.util.HashMap;
import java.util.Map;

import endpoint.SimpleObject;
import endpoint.Target;

@Target(SimpleObject.class)
public class SimpleObjectTransformer extends Transformer {

	public Map<Long, String> simple(SimpleObject object) {
		Map<Long, String> map = new HashMap<Long, String>();
		map.put(object.getId(), object.getaString());
		return map;
	}

}
