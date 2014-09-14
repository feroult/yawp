package endpoint.servlet.parent;

import endpoint.repository.models.parents.Parent;
import endpoint.servlet.ServletTestCase;

public class ParentServletTestCase extends ServletTestCase {

	protected Parent saveParent(String name) {
		Parent parent = new Parent(name);
		r.save(parent);
		return parent;
	}

}
