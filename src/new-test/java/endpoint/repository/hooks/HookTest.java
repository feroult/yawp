package endpoint.repository.hooks;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.basic.BasicObject;
import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;
import endpoint.repository.models.parents.Parent;
import endpoint.utils.EndpointTestCase;

public class HookTest extends EndpointTestCase {

	@Test
	public void testAfterSave() {
		BasicObject object = new BasicObject("hook_test");

		r.saveWithHooks(object);

		assertEquals("xpto", object.getStringValue());
	}

	@Test
	public void testAllObjectsHook() {
		Parent parent = new Parent("hook_test");
		r.saveWithHooks(parent);

		Child child = new Child("hook_test");
		child.setParentId(parent.getId());
		r.saveWithHooks(child);

		Grandchild grandchild = new Grandchild("hook_test");
		grandchild.setChildId(child.getId());
		r.saveWithHooks(grandchild);

		assertEquals("xpto", parent.getName());
		assertEquals("xpto", child.getName());
		assertEquals("xpto", grandchild.getName());
	}

	@Test
	public void testBeforeQuery() {
		saveProduct("xpto");
		saveProduct("abcd");

		List<Product> products = r.queryWithHooks(Product.class).list();
		assertEquals(1, products.size());
		assertEquals("xpto", products.get(0).getName());
	}

	private Product saveProduct(String name) {
		Product product = new Product();
		product.setName(name);
		r.saveWithHooks(product);
		return product;
	}

}
