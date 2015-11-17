package io.yawp.repository.models.basic;

import io.yawp.repository.annotations.Endpoint;

import org.apache.commons.lang3.StringUtils;

@Endpoint(path = "/composed_subclasses")
public class ComposedSubClass extends ComposedSuperClass<ComposedSubClass> {

    public ComposedSubClass() {
        super(StringUtils.EMPTY);
    }

    public ComposedSubClass(String name) {
        super(name);
    }

}
