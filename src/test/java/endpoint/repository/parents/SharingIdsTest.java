package endpoint.repository.parents;

import static endpoint.repository.parents.Assertions.assertListEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import endpoint.repository.IdRef;
import endpoint.repository.parents.models.Address;
import endpoint.repository.parents.models.ContactInfo;
import endpoint.repository.parents.models.House;
import endpoint.repository.parents.models.Person;
import endpoint.repository.response.HttpResponse;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class SharingIdsTest extends EndpointTestCase {

	private MyEndpointServlet servlet;

	@Before
	public void before() {
		servlet = new MyEndpointServlet("endpoint.repository.parents.models");
		r = servlet.r();
		FixturesLoader.load(r);
	}

	@Test
	public void testListFromRoot() {
		HttpResponse response = servlet.execute("get", "/contacts", null, null);
		List<ContactInfo> contacts = JsonUtils.fromList(r, response.getText(), ContactInfo.class);
		assertListEquals(contacts, "feroult@gmail.com");
		System.out.println(contacts.get(0).getPersonId());
	}
	
	@Test
	public void testGetFromRoot() {
		HttpResponse response = servlet.execute("get", "/contacts/4", null, null);
		ContactInfo contact = JsonUtils.from(r, response.getText(), ContactInfo.class);
		assertEquals("feroult@gmail.com", contact.getEmail());
		Person person = contact.getPersonId().fetch();
		assertEquals("Fernando", person.getName());
		assertEquals(FixturesLoader.PEOPLE.get("Fernando").asLong(), person.getId().asLong());
	}
	
	@Test
	public void testListNested() {
		HttpResponse response = servlet.execute("get", "/houses", null, null);
		List<House> houses = JsonUtils.fromList(r, response.getText(), House.class);
		assertListEquals(houses, "A red colored 2-store house");
		IdRef<Person> luan = FixturesLoader.PEOPLE.get("Luan");
		IdRef<Address> addressId = r.query(Address.class).from(luan).only().getId();
		assertEquals(addressId.asLong(), houses.get(0).getAddressId().asLong());
	}
}
