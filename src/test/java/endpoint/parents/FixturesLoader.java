package endpoint.parents;

import endpoint.IdRef;
import endpoint.Repository;
import endpoint.parents.models.Address;
import endpoint.parents.models.Person;

public final class FixturesLoader {

	private FixturesLoader() { throw new RuntimeException("Should not be instanciated."); }

	public static void load(Repository r) {
		loadPeople(r);
		loadAddresses(r);
	}

	public static void loadPeople(Repository r) {
		r.save(new Person("Luan", 18));
		r.save(new Person("Guilherme", 23));
		r.save(new Person("Fernando", 45));
		r.save(new Person("Paulo", 98));
		r.save(new Person("Raoni", Integer.MAX_VALUE));
	}

	public static void loadAddresses(Repository r) {
		r.save(new Address("Advovsk Street", 18, "NY", personByName(r, "Luan")));
		r.save(new Address("Street 2", 11, "Vegas", personByName(r, "Guilherme")));
		r.save(new Address("7th Avenue", 200, "NY", personByName(r, "Guilherme")));
	}

	private static IdRef<Person> personByName(Repository r, String name) {
		return r.query(Person.class).where("name", "=", name).only().getId();
	}
}
