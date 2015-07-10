package io.yawp.servlet.child;

import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Parent;

import org.junit.Before;

public class ChildServletTestCase extends ServletTestCase {

	protected Parent parent;

	@Before
	public void before() {
		parent = new Parent();
		yawp.save(parent);
	}

	protected Parent saveParent() {
		Parent parent = new Parent();
		yawp.save(parent);
		return parent;
	}

	protected Child saveChild(String name, Parent parent) {
		Child child = new Child(name);
		child.setParentId(parent.getId());
		yawp.save(child);
		return child;
	}

}
