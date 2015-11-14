package ${package}.utils;

import io.yawp.testing.EndpointTestCaseBase;

public class EndpointTestCase extends EndpointTestCaseBase {

	@Override
	protected String getAppPackage() {
		return "${package}";
	}

}
