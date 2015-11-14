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
		assertEquals("/some/base/dir/person/Person.java", new EndpointNaming("Person").getFilename("/some/base/dir"));
	}

	@Test
	public void testEndpointTestName() {
		assertEquals("PersonTest", new EndpointNaming("Person").getTestName());
		assertEquals("PersonAddressTest", new EndpointNaming("PersonAddress").getTestName());
	}

	@Test
	public void testEndpointTestFilename() {
		assertEquals("person/PersonTest.java", new EndpointNaming("Person").getTestFilename());
		assertEquals("personaddress/PersonAddressTest.java", new EndpointNaming("PersonAddress").getTestFilename());
		assertEquals("/some/base/dir/person/PersonTest.java", new EndpointNaming("Person").getTestFilename("/some/base/dir"));
	}

	@Test
	public void testEndpointInstance() {
		assertEquals("person", new EndpointNaming("Person").getInstance());
		assertEquals("personAddress", new EndpointNaming("PersonAddress").getInstance());
	}

}
