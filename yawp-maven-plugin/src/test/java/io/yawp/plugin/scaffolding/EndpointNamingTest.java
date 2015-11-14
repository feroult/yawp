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

	@Test
	public void testEndpointPath() {
		assertEquals("people", new EndpointNaming("Person").getPath());
		assertEquals("person-addresses", new EndpointNaming("PersonAddress").getPath());
		assertEquals("parents", new EndpointNaming("Parent").getPath());
		assertEquals("parent-addresses", new EndpointNaming("ParentAddress").getPath());
		assertEquals("children", new EndpointNaming("Child").getPath());
		assertEquals("grandchildren", new EndpointNaming("Grandchild").getPath());
	}

	@Test
	public void testEndpointFilename() {
		assertEquals("person/Person.java", new EndpointNaming("Person").getFilename());
		assertEquals("personaddress/PersonAddress.java", new EndpointNaming("PersonAddress").getFilename());
	}

}
