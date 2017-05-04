package io.yawp.repository.transformers.basic;

import io.yawp.repository.models.basic.Product;
import io.yawp.repository.transformers.Transformer;

public class ProductTransformer extends Transformer<Product> {

    @Override
    public Object defaults(Product product) {
        product.setName("default " + product.getName());
        return product;
    }

    @Override
    public Object index(Product product) {
        product.setName("default index " + product.getName());
        return product;
    }

}
