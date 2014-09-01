package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;

@Endpoint(path = "/contacts")
public class ContactInfo {

	@Id
	private IdRef<Person> personId;

	private String telephone, mobile;

	private String email;

	private String facebook;

	@SuppressWarnings("unused")
	private ContactInfo() { }

	public ContactInfo(IdRef<Person> personId, String telephone, String mobile, String email, String facebook) {
		this.personId = personId;
		this.telephone = telephone;
		this.mobile = mobile;
		this.email = email;
		this.facebook = facebook;
	}

	public IdRef<Person> getPersonId() {
		return personId;
	}

	public void setPersonId(IdRef<Person> personId) {
		this.personId = personId;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}
	
	@Override
	public String toString() {
		return this.email;
	}
}
