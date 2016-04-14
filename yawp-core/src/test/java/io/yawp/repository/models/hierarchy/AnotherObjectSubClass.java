package io.yawp.repository.models.hierarchy;

import io.yawp.repository.annotations.Endpoint;
import org.apache.commons.lang3.StringUtils;

@Endpoint(path = "/hierarchy_another-subclasses")
public class AnotherObjectSubClass extends ObjectSuperClass<AnotherObjectSubClass> {

    public AnotherObjectSubClass() {
        super(StringUtils.EMPTY);
    }

    public AnotherObjectSubClass(String name) {
        super(name);
    }

}
