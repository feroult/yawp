package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.actions.Action;
import endpoint.repository.actions.annotations.GET;
import endpoint.repository.actions.annotations.PUT;

public class AddressAction extends Action<Address>{

	@PUT("addAge")
	public void addAgeToAddress(IdRef<Address> addressId) {
		Address address = addressId.fetch();
		address.setNumber(address.getNumber() + address.getOwner().fetch().getAge());
		r.save(address);
	}

	@PUT("newyorkfy")
	public void setCityToNY(IdRef<Address> addressId) {
		Address address = addressId.fetch();
		address.setCity("NY");
		r.save(address);
	}
	
	@GET(value = "totalNumbers", overCollection = true)
	public Long totalNumbers(IdRef<Person> parentId) {
		long sum = 0;
		for (Address a : r.query(Address.class).from(parentId).list()) {
			sum += a.getNumber();
		}
		return sum;
	}
}
