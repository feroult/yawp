package endpoint.parents;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import endpoint.DatastoreServlet;
import endpoint.Repository;
import endpoint.parents.models.Address;
import endpoint.parents.models.Person;
import endpoint.response.HttpResponse;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class APITest extends EndpointTestCase {
	private MyDatastoreServlet servlet;

	private static class MyDatastoreServlet extends DatastoreServlet {
		private static final long serialVersionUID = 2770293412557653563L;

		public MyDatastoreServlet(String t) {
			super(t);
		}

		public HttpResponse execute(String method, String path, String requestJson, Map<String, String> params) {
			return super.execute(method, path, requestJson, params);
		}
		
		public Repository r() {
			return super.getRepository(null);
		}
	}

	private static final Map<String, Long> OWNERS = new HashMap<>();
	static {
		OWNERS.put("7th Avenue, 200 - NY", 2l);
		OWNERS.put("Street 2, 11 - Vegas", 2l);
		OWNERS.put("Advovsk Street, 18 - NY", 1l);
	};

	private static void assertListEmpty(List<?> list) {
		assertEquals(0, list.size());
	}

	private static void assertListEquals(List<?> list, String... expected) {
		assertEquals(expected.length, list.size());

		List<String> values = new ArrayList<>(list.size());
		for (Object o : list) {
			values.add(o.toString());
		}
		Collections.sort(values);
		List<String> expectedList = Arrays.asList(expected);
		Collections.sort(expectedList);
		
		for (int i = 0; i < list.size(); i++) {
			assertEquals(expectedList.get(i), values.get(i));
		}
	}

	private void assertParentIdEquals(List<Address> addresses, Long parentId) {
		for (Address address : addresses) {
			assertEquals(parentId, address.getOwner().asLong());
		}
	}

	@Before
	public void before() {
		servlet = new MyDatastoreServlet("endpoint.parents.models");
		r = servlet.r();
		FixturesLoader.load(r);
	}

	@Test
	public void testGetUnestedResourceCollection() {
		final String[] names = { "Fernando", "Guilherme", "Luan", "Paulo", "Raoni" };
		HttpResponse response = servlet.execute("get", "/people", null, null);
		List<Person> people = JsonUtils.fromList(r, response.getText(), Person.class);
		assertListEquals(people, names);
	}

	@Test
	public void testGetUnestedResourceElement() {
		HttpResponse response = servlet.execute("get", "/people/2", null, null);
		Person person = JsonUtils.from(r, response.getText(), Person.class);
		assertEquals("Guilherme", person.getName());
	}
	
	@Test
	public void testGetNestedResourcesListMany() {
		HttpResponse response = servlet.execute("get", "/people/2/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEquals(addresses, "7th Avenue, 200 - NY", "Street 2, 11 - Vegas");
		assertParentIdEquals(addresses, 2l);
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
		HttpResponse response = servlet.execute("get", "/people/3/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEmpty(addresses);
	}

	@Test
	public void testGetNestedResourcesFromRootListAll() {
		HttpResponse response = servlet.execute("get", "/addresses", null, null);
		List<Address> addresses = JsonUtils.fromList(r, response.getText(), Address.class);
		assertListEquals(addresses, "7th Avenue, 200 - NY", "Street 2, 11 - Vegas", "Advovsk Street, 18 - NY");

		for (Address address : addresses) {
			assertEquals(OWNERS.get(address.toString()), address.getOwner().asLong());
		}
	}

	@Test @Ignore
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
			assertEquals(OWNERS.get(address.toString()), address.getOwner().asLong());
		}
	}
}
