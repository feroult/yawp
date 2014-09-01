package endpoint.repository.parents;

import java.util.HashMap;
import java.util.Map;

import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.parents.models.Address;
import endpoint.repository.parents.models.ContactInfo;
import endpoint.repository.parents.models.House;
import endpoint.repository.parents.models.Person;

public final class FixturesLoader {

	public static final Map<String, IdRef<Person>> PEOPLE = new HashMap<>();
	public static final Map<String, Long> OWNERS = new HashMap<>();
	static {
		OWNERS.put("7th Avenue, 200 - NY", 3l);
		OWNERS.put("Street 2, 11 - Vegas", 3l);
		OWNERS.put("Advovsk Street, 18 - NY", 1l);
	};

	private FixturesLoader() {
		throw new RuntimeException("Should not be instanciated.");
	}

	public static void load(Repository r) {
		PEOPLE.clear();
		loadPeople(r);
		loadAddressesAndHouses(r);
		loadContacts(r);
	}

	public static void loadPeople(Repository r) {
		createPerson(r, "Luan", 18);
		createPerson(r, "Leonardo", 18);
		createPerson(r, "Guilherme", 23);
		createPerson(r, "Fernando", 45);
		createPerson(r, "Paulo", 98);
		createPerson(r, "Raoni", Integer.MAX_VALUE);
	}

	private static void createPerson(Repository r, String name, int age) {
		Person person = new Person(name, age);
		r.save(person);
		PEOPLE.put(person.getName(), person.getId());
	}

	public static void loadContacts(Repository r) {
		r.save(new ContactInfo(personByName(r, "Fernando"), "1111", "2222", "feroult@gmail.com", "feroult"));
	}

	public static void loadAddressesAndHouses(Repository r) {
		Address luansAddress = new Address("Advovsk Street", 18, "NY", personByName(r, "Luan"));
		r.save(luansAddress);
		r.save(new House(luansAddress.getId(), 2, "red"));

		r.save(new Address("Street 2", 11, "Vegas", personByName(r, "Guilherme")));
		r.save(new Address("7th Avenue", 200, "NY", personByName(r, "Guilherme")));
	}

	private static IdRef<Person> personByName(Repository r, String name) {
		return r.query(Person.class).where("name", "=", name).only().getId();
	}
}
