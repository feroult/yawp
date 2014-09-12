package endpoint.repository.hooks;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.basic.BasicObject;
import endpoint.utils.EndpointTestCase;

public class HookTest extends EndpointTestCase {

	@Test
	public void testAfterSave() {
		BasicObject object = new BasicObject("hook_test");

		r.saveWithHooks(object);

		assertEquals("xpto", object.getStringValue());
	}

	@Test
	public void testAllTargetsHook() {
		Product product = saveProduct("xpto");
		assertEquals("xpto GenericHook touch", product.getName());
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
