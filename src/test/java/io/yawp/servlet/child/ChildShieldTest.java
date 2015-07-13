package io.yawp.servlet.child;

import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.models.parents.ShieldedChild;

import org.junit.Ignore;
import org.junit.Test;

public class ChildShieldTest extends ServletTestCase {

	@Test
	public void testIndex() {
		assertGetWithStatus("/parents/1/shielded_children", 404);
		assertGetWithStatus("/parents/100/shielded_children", 200);
	}

	@Test
	public void testShow() {
		Parent parent = saveParent(1l);
		saveShieldedChild(1l, parent);
		saveShieldedChild(100l, parent);

		assertGetWithStatus("/parents/1/shielded_children/1", 404);
		assertGetWithStatus("/parents/1/shielded_children/100", 200);
	}

	@Test
	public void testActions() {
		Parent parent = saveParent(1l);
		saveShieldedChild(1l, parent);
		saveShieldedChild(100l, parent);
		saveParent(100l);

		assertPutWithStatus("/parents/1/shielded_children/collection", 404);
		assertPutWithStatus("/parents/100/shielded_children/collection", 200);

		assertPutWithStatus("/parents/1/shielded_children/1/single", 404);
		assertPutWithStatus("/parents/1/shielded_children/100/single", 200);
	}

	@Test
	@Ignore
	public void testActionWhereOnExistingParentObject() {
		saveShieldedChild(1l, saveParent(1l, "ok-for-janis"));
		saveShieldedChild(2l, saveParent(2l, "ok-for-amy"));

		login("janis", "rock.com");

		//assertPutWithStatus("/parents/1/shielded_children/1/single", 200);
		assertPutWithStatus("/parents/1/shielded_children/collection", 500);

		//assertPutWithStatus("/parents/2/shielded_children/2/single", 404);
		//assertPutWithStatus("/parents/2/shielded_children/collection", 500);
	}

	private void saveShieldedChild(long id, Parent parent) {
		ShieldedChild child = new ShieldedChild();
		child.setId(parent.getId().createChildId(ShieldedChild.class, id));
		child.setParentId(parent.getId());
		yawp.save(child);
	}

	private Parent saveParent(long id) {
		return saveParent(id, null);
	}

	private Parent saveParent(long id, String name) {
		Parent parent = new Parent();
		parent.setId(IdRef.create(yawp, Parent.class, id));
		parent.setName(name);
		yawp.save(parent);
		return parent;
	}

}
