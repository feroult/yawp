package io.yawp.servlet.grandchild;

import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Parent;

import org.junit.Before;

public class GrandchildServletTestCase extends ServletTestCase {

    protected Parent parent;
    protected Child child;

    @Before
    public void before() {
        parent = new Parent();
        yawp.save(parent);

        child = new Child();
        child.setParentId(parent.getId());
        yawp.save(child);
    }

    protected Parent saveParent() {
        Parent parent = new Parent();
        yawp.save(parent);
        return parent;
    }

    protected Child saveChild() {
        return saveChild(parent);
    }

    protected Child saveChild(Parent parent) {
        Child child = new Child();
        child.setParentId(parent.getId());
        yawp.save(child);
        return child;
    }

    protected Grandchild saveGrandchild(String name, Child child) {
        Grandchild grandchild = new Grandchild(name);
        grandchild.setChildId(child.getId());
        yawp.save(grandchild);
        return grandchild;
    }

}
