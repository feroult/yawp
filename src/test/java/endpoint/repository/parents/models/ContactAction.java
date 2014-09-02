package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.actions.Action;
import endpoint.repository.actions.annotations.GET;

public class ContactAction extends Action<ContactInfo> {

	@GET("facebookUrl")
	public String getFacebookUrl(IdRef<Person> info) {
		return "http://www.facebook.com/" + info.fetch(ContactInfo.class).getFacebook();
	}
}
