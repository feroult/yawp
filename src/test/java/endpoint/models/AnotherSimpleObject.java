package endpoint.models;

import static org.junit.Assert.assertEquals;
import endpoint.models.DatastoreObject;

public class AnotherSimpleObject extends DatastoreObject {

	private String aString;

	public AnotherSimpleObject() {

	}

	public AnotherSimpleObject(String aString) {
		super();
		this.aString = aString;
	}

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

	public void assertAnotherObject(String aString) {
		assertEquals(AnotherSimpleObject.class.getSimpleName(), getKey().getKind());
		assertFields(aString);
	}

	private void assertFields(String aString) {
		assertEquals(aString, getaString());
	}

}
