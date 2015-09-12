package io.yawp.commons.utils.kind;

import io.yawp.repository.Repository;

public abstract class KindResolver {

	public abstract String getKind(Class<?> clazz);

	public abstract String getPath(String kind);

	private static final String KINDRESOLVER_SETTING_KEY = "yawp.kindresolver";

	private static KindResolver kindResolver;

	static {
		loadKindResolver();
	}

	private static void loadKindResolver() {
		String kindResolverClazzName = System.getProperty(KINDRESOLVER_SETTING_KEY);
		if (kindResolverClazzName == null) {
			kindResolver = new DefaultKindResolver();
			return;
		}
		try {
			kindResolver = (KindResolver) Class.forName(kindResolverClazzName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException("Invalid kind resolver: " + kindResolverClazzName, e);
		}
	}

	public static String getKindFromClass(Class<?> clazz) {
		return kindResolver.getKind(clazz);
	}

	public static Class<?> getClassFromKind(Repository r, String kind) {
		String endpointPath = kindResolver.getPath(kind);
		return r.getFeatures().get(endpointPath).getClazz();
	}

}
