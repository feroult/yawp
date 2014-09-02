package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.actions.Action;
import endpoint.repository.actions.annotations.PUT;

public class HouseAction extends Action<House> {

	@PUT("buildStorePerYear")
	public void buildStorePerYear(IdRef<Address> addressId) {
		House house = addressId.fetch(House.class);
		Person owner = house.getOwner().fetch();
		house.setFloors(house.getFloors() + owner.getAge());
		r.save(house);
	}
	
	@PUT("buildStorePerAddressNumber")
	public void buildStorePerAddressNumber(IdRef<Address> addressId) {
		House house = addressId.fetch(House.class);
		Address address = addressId.fetch();
		house.setFloors(house.getFloors() + address.getNumber());
		r.save(house);
	}
}
