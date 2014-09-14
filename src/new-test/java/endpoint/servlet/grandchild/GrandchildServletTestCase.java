package endpoint.servlet.grandchild;

import org.junit.Before;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;
import endpoint.repository.models.parents.Parent;
import endpoint.servlet.ServletTestCase;

public class GrandchildServletTestCase extends ServletTestCase {

	protected Parent parent;
	protected Child child;

	@Before
	public void before() {
		parent = new Parent();
		r.save(parent);

		child = new Child();
		child.setParentId(parent.getId());
		r.save(child);
	}

	protected Child saveChild() {
		Child child = new Child();
		child.setParentId(parent.getId());
		r.save(child);
		return child;
	}

	protected Grandchild saveGrandchild(String name, Child child) {
		Grandchild grandchild = new Grandchild(name);
		grandchild.setChildId(child.getId());
		r.save(grandchild);
		return grandchild;
	}

}
