package io.yawp.servlet.parent;

import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.models.parents.Parent;

public class ParentServletTestCase extends ServletTestCase {

    protected Parent saveParent(String name) {
        Parent parent = new Parent(name);
        yawp.save(parent);
        return parent;
    }

}
