package endpoint;

import endpoint.annotations.Endpoint;
import endpoint.annotations.Id;
import endpoint.annotations.Parent;

@Endpoint
public class GrandGrandChild {
	@Id IdRef<GrandGrandChild> id;
	String text;
	@Parent IdRef<GrandChildObjectWithIdRef> parent;
	
	@SuppressWarnings("unused")
	private GrandGrandChild() {}
	
	GrandGrandChild(String text, IdRef<GrandChildObjectWithIdRef> parent) {
		this.text = text;
		this.parent = parent;
	}
}