package ${yawpPackage}.models.${endpoint.packageName};

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.yawp.testing.EndpointTestCase;

import ${yawpPackage}.models.${endpoint.packageName}.${endpoint.name};

import org.junit.Test;

public class $endpoint.testName extends EndpointTestCase {

	@Override
	protected String getAppPackage() {
		return "$yawpPackage";
	}

	@Test
	public void testCreate() {
		String json = post("/$endpoint.path", "{}");
		Person $endpoint.instance = from(json, ${endpoint.name}.class);

		assertNotNull(person);
	}
}
