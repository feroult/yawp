package endpoint.repository;

import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.ParentId;

@Endpoint(path = "/grandgrandchilds")
public class GrandGrandChild {
	@Id
	IdRef<GrandGrandChild> id;
	String text;
	@ParentId
	IdRef<GrandChildObjectWithIdRef> parent;

	@SuppressWarnings("unused")
	private GrandGrandChild() {
	}

	GrandGrandChild(String text, IdRef<GrandChildObjectWithIdRef> parent) {
		this.text = text;
		this.parent = parent;
	}
}