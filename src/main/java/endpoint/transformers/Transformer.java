package endpoint.transformers;

import endpoint.Repository;

public class Transformer {

	protected Repository r;

	public void setRepository(Repository r) {
		this.r = r;
	}

	public <T extends Transformer> T get(Class<T> clazz) {
		try {
			T transformer = clazz.newInstance();
			transformer.setRepository(r);
			return transformer;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
