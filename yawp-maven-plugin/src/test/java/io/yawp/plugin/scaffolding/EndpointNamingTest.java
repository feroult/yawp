package io.yawp.plugin.scaffolding;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EndpointNamingTest {

	@Test
	public void testName() {
		assertEquals("Person", new EndpointNaming("person").getName());
		assertEquals("Person", new EndpointNaming("Person").getName());
		assertEquals("PersonAddress", new EndpointNaming("personAddress").getName());
		assertEquals("PersonAddress", new EndpointNaming("PersonAddress").getName());
		assertEquals("PersonAddress", new EndpointNaming("person_address").getName());
	}

	@Test
	public void testEndpointPackage() {
		assertEquals("person", new EndpointNaming("Person").getPackageName());
		assertEquals("personaddress", new EndpointNaming("PersonAddress").getPackageName());

	}

}
