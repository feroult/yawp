package io.yawp.servlet.child;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.models.parents.ShieldedChild;
import io.yawp.utils.ServletTestCase;

import org.junit.Test;

public class ChildShieldTest extends ServletTestCase {

	@Test
	public void testIndex() {
		assertGetWithStatus("/parents/1/shielded_children", 404);
		assertGetWithStatus("/parents/100/shielded_children", 200);
	}

	@Test
	public void testShow() {
		Parent parent = createParent(1l);
		createShieldedChild(1l, parent);
		createShieldedChild(100l, parent);

		assertGetWithStatus("/parents/1/shielded_children/1", 404);
		assertGetWithStatus("/parents/1/shielded_children/100", 200);
	}

	@Test
	public void testActions() {
		Parent parent = createParent(1l);
		createShieldedChild(1l, parent);
		createShieldedChild(100l, parent);
		createParent(100l);

		assertPutWithStatus("/parents/1/shielded_children/collection", 404);
		assertPutWithStatus("/parents/100/shielded_children/collection", 200);

		assertPutWithStatus("/parents/1/shielded_children/1/single", 404);
		assertPutWithStatus("/parents/1/shielded_children/100/single", 200);
	}

	private void createShieldedChild(long id, Parent parent) {
		ShieldedChild child = new ShieldedChild();
		child.setId(parent.getId().createChildId(ShieldedChild.class, id));
		child.setParentId(parent.getId());
		yawp.save(child);
	}

	private Parent createParent(long id) {
		Parent parent = new Parent();
		parent.setId(IdRef.create(yawp, Parent.class, id));
		yawp.save(parent);
		return parent;
	}

}
