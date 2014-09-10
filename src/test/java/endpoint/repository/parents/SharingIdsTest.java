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
		assertEquals("Fernando", contacts.get(0).getPersonId().fetch().getName());
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
		assertEquals(Address.class, houses.get(0).getAddressId().getClazz());
	}

	@Test
	public void testPost() {
		IdRef<Person> leo = FixturesLoader.PEOPLE.get("Leonardo");
		HttpResponse response = servlet.execute("post", "/contacts/" + leo.asLong(), "{ telephone : 1234, facebook : 'leo'  }", null);
		ContactInfo info = JsonUtils.from(r, response.getText(), ContactInfo.class);
		assertEquals("1234", info.getTelephone());
		assertEquals("leo", info.getFacebook());
		assertEquals("Leonardo", info.getPersonId().fetch().getName());
		response = servlet.execute("get", "/contacts/" + leo.asLong(), null, null);
		ContactInfo retrievedInfo = JsonUtils.from(r, response.getText(), ContactInfo.class);
		assertEquals("1234", retrievedInfo.getTelephone());
		assertEquals("leo", retrievedInfo.getFacebook());
		assertEquals("Leonardo", retrievedInfo.getPersonId().fetch().getName());
	}

	@Test
	public void testPostNested() {
		IdRef<Person> luan = FixturesLoader.PEOPLE.get("Luan");
		HttpResponse response = servlet.execute("post", "/people/" + luan.asLong() + "/houses/100", "{ color : \"Magenta\", floors : 4  }", null);
		House house = JsonUtils.from(r, response.getText(), House.class);
		assertEquals("Magenta", house.getColor());
		assertEquals(4, house.getFloors());
		assertEquals("Luan", house.getOwner().fetch().getName());
		response = servlet.execute("get", "/people/" + luan.asLong() + "/houses/100", null, null);
		House retrievedHouse = JsonUtils.from(r, response.getText(), House.class);
		assertEquals("Magenta", retrievedHouse.getColor());
		assertEquals(4, retrievedHouse.getFloors());
		assertEquals("Luan", retrievedHouse.getOwner().fetch().getName());

		response = servlet.execute("get", "/houses/", null, null);
		List<House> houses = JsonUtils.fromList(r, response.getText(), House.class);
		assertEquals(2, houses.size());
	}

	@Test
	public void testPut() {
		IdRef<Person> fer = FixturesLoader.PEOPLE.get("Fernando");
		HttpResponse response = servlet.execute("put", "/contacts/" + fer.asLong(), "{ telephone : 4321, facebook : 'fernando'  }", null);
		ContactInfo info = JsonUtils.from(r, response.getText(), ContactInfo.class);
		assertEquals("4321", info.getTelephone());
		assertEquals("fernando", info.getFacebook());
		assertEquals("Fernando", info.getPersonId().fetch().getName());
		response = servlet.execute("get", "/contacts/" + fer.asLong(), null, null);
		ContactInfo retrievedInfo = JsonUtils.from(r, response.getText(), ContactInfo.class);
		assertEquals("4321", retrievedInfo.getTelephone());
		assertEquals("fernando", retrievedInfo.getFacebook());
		assertEquals("Fernando", retrievedInfo.getPersonId().fetch().getName());
	}

	@Test
	public void testPutNested() {
		IdRef<Person> luan = FixturesLoader.PEOPLE.get("Luan");
		HttpResponse response = servlet.execute("put", "/people/" + luan.asLong() + "/houses/7", "{ color : \"Pink\", floors : 1  }", null);
		House house = JsonUtils.from(r, response.getText(), House.class);
		assertEquals("Pink", house.getColor());
		assertEquals(1, house.getFloors());
		assertEquals("Luan", house.getOwner().fetch().getName());
		response = servlet.execute("get", "/people/" + luan.asLong() + "/houses/7", null, null);
		House retrievedHouse = JsonUtils.from(r, response.getText(), House.class);
		assertEquals("Pink", retrievedHouse.getColor());
		assertEquals(1, retrievedHouse.getFloors());
		assertEquals("Luan", retrievedHouse.getOwner().fetch().getName());

		response = servlet.execute("get", "/houses/", null, null);
		List<House> houses = JsonUtils.fromList(r, response.getText(), House.class);
		assertListEquals(houses, "A Pink colored 1-store house");
	}

	@Test
	public void testCustomAction() {
		IdRef<Person> fer = FixturesLoader.PEOPLE.get("Fernando");
		HttpResponse response = servlet.execute("get", "/contacts/" + fer.asLong() + "/facebookUrl", null, null);
		assertEquals("\"http://www.facebook.com/feroult\"", response.getText());
	}

	@Test
	public void testCustomActionNestedUsingParent() {
		IdRef<Person> luan = FixturesLoader.PEOPLE.get("Luan");
		servlet.execute("put", "/people/" + luan.asLong() + "/houses/7/buildStorePerYear", null, null);
		HttpResponse response = servlet.execute("get", "/people/" + luan.asLong() + "/houses/7", null, null);
		House house = JsonUtils.from(r, response.getText(), House.class);
		assertEquals(20, house.getFloors());
	}
	
	@Test
	public void testCustomActionNestedUsingSibiling() {
		IdRef<Person> luan = FixturesLoader.PEOPLE.get("Luan");
		servlet.execute("put", "/people/" + luan.asLong() + "/houses/7/buildStorePerAddressNumber", null, null);
		HttpResponse response = servlet.execute("get", "/people/" + luan.asLong() + "/houses/7", null, null);
		House house = JsonUtils.from(r, response.getText(), House.class);
		assertEquals(20, house.getFloors());
	}
}
