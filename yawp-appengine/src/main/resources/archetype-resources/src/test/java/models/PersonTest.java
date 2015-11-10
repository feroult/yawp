package ${package}.models;

import static org.junit.Assert.assertEquals;
import io.yawp.testing.EndpointTestCase;

import org.junit.Test;

public class PersonTest extends EndpointTestCase {

	@Override
	protected String getAppPackage() {
		return "yawptut";
	}

	@Test
	public void testExample1() {
		String json = post("/people", "{ name: 'janes' } ");
		Person person = from(json, Person.class);

		assertEquals("janes", person.getName());
	}

	@Test
	public void testExample2() {
		Person janes = new Person();
		janes.setName("janes");
		yawp.save(janes);

		Person person = yawp(Person.class).first();

		assertEquals("janes", person.getName());
	}
}
