package io.yawp.plugin.scaffolding;

import static org.junit.Assert.assertEquals;
import io.yawp.plugin.scaffolding.EndpointNaming;

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

	@Test
	public void testEndpointTestName() {
		assertEquals("PersonTest", new EndpointNaming("Person").getTestName());
		assertEquals("PersonAddressTest", new EndpointNaming("PersonAddress").getTestName());
	}

	@Test
	public void testEndpointTestFilename() {
		assertEquals("person/PersonTest.java", new EndpointNaming("Person").getTestFilename());
		assertEquals("personaddress/PersonAddressTest.java", new EndpointNaming("PersonAddress").getTestFilename());
	}

	@Test
	public void testEndpointInstance() {
		assertEquals("person", new EndpointNaming("Person").getInstance());
		assertEquals("personAddress", new EndpointNaming("PersonAddress").getInstance());
	}

	@Test
	public void testEndpointShieldName() {
		assertEquals("PersonShield", new EndpointNaming("Person").getShieldName());
		assertEquals("PersonAddressShield", new EndpointNaming("PersonAddress").getShieldName());
	}

	@Test
	public void testEndpointShieldFilename() {
		assertEquals("person/PersonShield.java", new EndpointNaming("Person").getShieldFilename());
		assertEquals("personaddress/PersonAddressShield.java", new EndpointNaming("PersonAddress").getShieldFilename());
	}

	@Test
	public void testEndpointActionName() {
		assertEquals("PersonActivateAction", new EndpointNaming("Person").action("activate").getActionName());
		assertEquals("PersonAddressActivateAction", new EndpointNaming("PersonAddress").action("activate").getActionName());

		assertEquals("PersonActivateAllAction", new EndpointNaming("Person").action("ActivateAll").getActionName());
		assertEquals("PersonAddressActivateAllAction", new EndpointNaming("PersonAddress").action("activate_all").getActionName());
		assertEquals("PersonAddressActivateAllAction", new EndpointNaming("PersonAddress").action("activateAll").getActionName());
	}

	@Test
	public void testEndpointActionFilename() {
		assertEquals("person/PersonActivateAction.java", new EndpointNaming("Person").action("activate").getActionFilename());
		assertEquals("personaddress/PersonAddressActivateAction.java", new EndpointNaming("PersonAddress").action("activate").getActionFilename());

		assertEquals("person/PersonActivateAllAction.java", new EndpointNaming("Person").action("ActivateAll").getActionFilename());
		assertEquals("personaddress/PersonAddressActivateAllAction.java", new EndpointNaming("PersonAddress").action("activate_all")
				.getActionFilename());
		assertEquals("personaddress/PersonAddressActivateAllAction.java", new EndpointNaming("PersonAddress").action("activateAll")
				.getActionFilename());
	}

}
