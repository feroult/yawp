package endpoint.repository.parents;

import static endpoint.repository.parents.Assertions.assertError;
import static endpoint.repository.parents.Assertions.assertListEmpty;
import static endpoint.repository.parents.Assertions.assertListEquals;
import static endpoint.repository.parents.Assertions.assertParentIdEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import endpoint.repository.parents.models.Address;
import endpoint.repository.parents.models.Person;
import endpoint.repository.response.HttpResponse;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class BaseAPITest extends EndpointTestCase {
	private MyEndpointServlet servlet;

	@Before
	public void before() {
		servlet = new MyEndpointServlet("endpoint.repository.parents.models");
		r = servlet.r();
		FixturesLoader.load(r);
	}

	@Test
	public void testGetUnestedResourceCollection() {
		final String[] names = { "Fernando", "Guilherme", "Leonardo", "Luan", "Paulo", "Raoni" };
		HttpResponse response = servlet.execute("get", "/people", null, null);
		List<Person> people = JsonUtils.fromList(r, response.getText(), Person.class);
		assertListEquals(people, names);
	}

	@Test
	public void testGetUnestedResourceElement() {
		HttpResponse response = servlet.execute("get", "/people/3", null, null);
		Person person = JsonUtils.from(r, response.getText(), Person.class);
		assertEquals("Guilherme", person.getName());
	}

	@Test
	public void testGetNestedResourcesListMany() {
		HttpResponse response = servlet.execute("get", "/people/3/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEquals(addresses, "7th Avenue, 200 - NY", "Street 2, 11 - Vegas");
		assertParentIdEquals(addresses, 3l);
	}

	@Test
	public void testGetNestedResourcesListOne() {
		HttpResponse response = servlet.execute("get", "/people/1/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEquals(addresses, "Advovsk Street, 18 - NY");
		assertParentIdEquals(addresses, 1l);
	}

	@Test
	public void testGetNestedResourcesListNone() {
		HttpResponse response = servlet.execute("get", "/people/2/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEmpty(addresses);
	}

	@Test
	public void testGetNestedResourcesFromRootListAll() {
		HttpResponse response = servlet.execute("get", "/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEquals(addresses, "7th Avenue, 200 - NY", "Street 2, 11 - Vegas", "Advovsk Street, 18 - NY");

		for (Address address : addresses) {
			assertEquals(FixturesLoader.OWNERS.get(address.toString()), address.getOwner().asLong());
		}
	}

	@Test
	public void testGetNestedResourcesFromRootListWithQuery() {
		final String where = "{where: {op: 'or', c: [{p: 'number', op: '=', v: 200}, {p: 'number', op: '<', v: 12}]}}";
		final Map<String, String> params = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("q", where);
			}
		};
		HttpResponse response = servlet.execute("get", "/addresses", null, params);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEquals(addresses, "7th Avenue, 200 - NY", "Street 2, 11 - Vegas");
		for (Address address : addresses) {
			assertEquals(FixturesLoader.OWNERS.get(address.toString()), address.getOwner().asLong());
		}
	}

	@Test
	public void testCreateNestedResources() {
		HttpResponse response = servlet.execute("post", "/people/3/addresses", "{ street : \"Times Square\", number : 21, city : \"NY\" }", null);
		Address address = JsonUtils.from(r, response.getText(), Address.class);
		assertEquals("Times Square, 21 - NY", address.toString());
		assertEquals((Long) 3l, address.getOwner().asLong());
		response = servlet.execute("get", "/people/3/addresses/" + address.getId().asLong(), null, null);
		Address retrievedAddress = JsonUtils.from(r, response.getText(), Address.class);
		assertEquals(address, retrievedAddress);
	}
	
	@Test
	public void testUpdateNestedResources() {
		HttpResponse response = servlet.execute("put", "/people/1/addresses/1", "{ id: 1, street : \"Advovsck Street\", number: 18, city: \"NY\"}", null);
		Address address = JsonUtils.from(r, response.getText(), Address.class);
		assertEquals("Advovsck Street, 18 - NY", address.toString());
		assertEquals((Long) 1l, address.getOwner().asLong());
		assertEquals((Long) 1l, address.getId().asLong());
		response = servlet.execute("get", "/people/1/addresses/1", null, null);
		Address retrievedAddress = JsonUtils.from(r, response.getText(), Address.class);
		assertEquals(address, retrievedAddress);
	}

	@Test
	public void testActionCallNestedResources() {
		servlet.execute("put", "/people/3/addresses/8/newyorkfy", null, null);
		HttpResponse response = servlet.execute("get", "/people/3/addresses/8", null, null);
		Address address = JsonUtils.from(r, response.getText(), Address.class);
		assertEquals("NY", address.getCity());
	}

	@Test
	public void testActionCallNestedUsingParentIdInAction() {
		HttpResponse response = servlet.execute("get", "/people/3/addresses/8", null, null);
		Address address = JsonUtils.from(r, response.getText(), Address.class);
		int preNumber = address.getNumber();
		servlet.execute("put", "/people/3/addresses/8/addAge", null, null);
		response = servlet.execute("get", "/people/3/addresses/8", null, null);
		address = JsonUtils.from(r, response.getText(), Address.class);
		assertEquals(preNumber + address.getOwner().fetch().getAge(), address.getNumber());
	}

	@Test
	public void testActionCallNestedActionOverCollection() {
		HttpResponse response = servlet.execute("get", "/people/3/addresses/totalNumbers", null, null);
		long sum = Long.parseLong(response.getText());
		assertEquals(211l, sum);
	}

	@Test
	public void testActionCallNestedActionOverCollectionFromRoot() {
		HttpResponse response = servlet.execute("get", "/addresses/totalNumbers", null, null);
		long sum = Long.parseLong(response.getText());
		assertEquals(229l, sum);
	}
	
	@Ignore @Test
	public void testDeletedNested() {
		servlet.execute("delete", "/people/3/addresses/8", null, null);
		assertError(servlet, "get", "/people/3/addresses/8", 404);
	}

	@Ignore @Test
	public void testDeletedWithChildren() {
		servlet.execute("delete", "/people/3", null, null);
		assertError(servlet, "get", "/people/3", 404);
		assertError(servlet, "get", "/people/3/addresses/8", 404);

		HttpResponse response = servlet.execute("get", "/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEquals(addresses, "7th Avenue, 200 - NY", "Street 2, 11 - Vegas", "Advovsk Street, 18 - NY");
	}
}
