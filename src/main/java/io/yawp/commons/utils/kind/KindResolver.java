package io.yawp.commons.utils.kind;

public interface KindResolver {

	public String getKind(Class<?> clazz);

	public String getPath(String kind);

}
