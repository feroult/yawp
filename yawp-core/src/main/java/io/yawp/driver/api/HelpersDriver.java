package io.yawp.driver.api;

import java.util.List;

public interface HelpersDriver {

	void deleteAll();

	void deleteAll(String namespace);

	List<String> listNamespaces();

	void sync();

}
