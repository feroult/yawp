package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;

@Endpoint(path = "/houses")
public class House {

	@Id
	private IdRef<Address> addressId;

	private int floors;
	private String color;

	@SuppressWarnings("unused")
	private House() { }

	public House(IdRef<Address> addressId, int floors, String color) {
		this.addressId = addressId;
		this.floors = floors;
		this.color = color;
	}

	public IdRef<Address> getAddressId() {
		return addressId;
	}

	public void setAddressId(IdRef<Address> addressId) {
		this.addressId = addressId;
	}

	public int getFloors() {
		return floors;
	}

	public void setFloors(int floors) {
		this.floors = floors;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public String toString() {
		return "A " + this.color + " colored " + this.floors + "-store house";
	}
}
