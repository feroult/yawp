package endpoint.servlet.child;

import org.junit.Before;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Parent;
import endpoint.servlet.ServletTestCase;

public class ChildServletTestCase extends ServletTestCase {

	protected Parent parent;

	@Before
	public void before() {
		parent = new Parent();
		r.save(parent);
	}

	protected Parent saveParent() {
		Parent parent = new Parent();
		r.save(parent);
		return parent;
	}

	protected Child saveChild(String name, Parent parent) {
		Child child = new Child(name);
		child.setParentId(parent.getId());
		r.save(child);
		return child;
	}

}
