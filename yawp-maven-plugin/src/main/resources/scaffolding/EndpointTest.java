package ${yawpPackage}.models.${endpoint.packageName};

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import yawpapp.utils.EndpointTestCase;

public class $endpoint.testName extends EndpointTestCase {

	@Test
	public void testCreate() {
		// TODO Auto-generated method stub
		String json = post("/$endpoint.path", "{}");
		Person $endpoint.instance = from(json, ${endpoint.name}.class);

		assertNotNull(person);
	}
}
