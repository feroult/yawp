package io.yawp.servlet.defaults;

import static org.junit.Assert.assertEquals;
import io.yawp.commons.utils.ServletTestCase;

import java.util.List;

import org.junit.Test;

public class DefaultsTest extends ServletTestCase {

	@Test
	public void testShow() {
		Product product = new Product("xpto");
		yawp.save(product);

		String json = get(uri("/products/%s", product));
		Product retrievedProduct = from(json, Product.class);

		assertEquals("default xpto", retrievedProduct.getName());
	}

	@Test
	public void testIndex() {
		Product product = new Product("xpto");
		yawp.save(product);

		String json = get(uri("/products"));
		List<Product> products = fromList(json, Product.class);

		assertEquals("default index xpto", products.get(0).getName());
	}
}
