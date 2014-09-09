package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.actions.Action;
import endpoint.repository.actions.annotations.PUT;

public class HouseAction extends Action<House> {

	@PUT("buildStorePerYear")
	public void buildStorePerYear(IdRef<House> id) {
		House house = id.fetch();
		Person owner = house.getOwner().fetch();
		house.setFloors(house.getFloors() + owner.getAge());
		r.save(house);
	}

	@PUT("buildStorePerAddressNumber")
	public void buildStorePerAddressNumber(IdRef<House> id) {
		House house = id.fetch();
		Address address = id.fetch(Address.class);
		house.setFloors(house.getFloors() + address.getNumber());
		r.save(house);
	}
}
