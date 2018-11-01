package io.yawp.tools.datastore;

import io.yawp.tools.Tool;

public class ListNamespacesTool extends Tool {

	@Override
	public void execute() {
		pw.println(yawp.driver().helpers().listNamespaces());
	}
}
