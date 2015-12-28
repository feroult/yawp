package io.yawp.repository.models.hierarchy;

import io.yawp.repository.annotations.Endpoint;

import org.apache.commons.lang3.StringUtils;

@Endpoint(path = "/hierarchy_subclasses")
public class ObjectSubClass extends ObjectSuperClass<ObjectSubClass> {

    public ObjectSubClass() {
        super(StringUtils.EMPTY);
    }

    public ObjectSubClass(String name) {
        super(name);
    }

}
