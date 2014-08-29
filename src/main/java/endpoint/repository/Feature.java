package endpoint.repository;

public class Feature {

	protected Repository r;

	public void setRepository(Repository r) {
		this.r = r;
	}

	public <T extends Feature> T feature(Class<T> clazz) {
		try {
			T feature = clazz.newInstance();
			feature.setRepository(r);
			return feature;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
