package io.yawp.repository.query;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.hierarchy.ObjectSubClass;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.condition.BaseCondition;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static io.yawp.repository.models.basic.BasicObject.saveManyBasicObjects;
import static io.yawp.repository.models.basic.BasicObject.saveOneObject;
import static io.yawp.repository.query.condition.Condition.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatastoreQueryTest extends EndpointTestCase {

    private BasicObject setId(BasicObject basicObject, long id) {
        basicObject.setId(IdRef.create(yawp, BasicObject.class, id));
        return basicObject;
    }

    @Test
    public void testWhere() {
        saveManyBasicObjects(3);
        yawp.save(new BasicObject("different"));

        List<BasicObject> objects = yawp(BasicObject.class).where("stringValue", "=", "xpto").list();

        assertEquals(3, objects.size());

        assertEquals(1, objects.get(0).getIntValue());
        assertEquals(2, objects.get(1).getIntValue());
        assertEquals(3, objects.get(2).getIntValue());
    }

    @Test
    public void testWithIdAsString() {
        BasicObject myObj = new BasicObject("xpto");
        yawp.save(myObj);

        BasicObject fetch = yawp(BasicObject.class).where("id", "=", myObj.getId().toString()).only();
        assertEquals("xpto", fetch.getStringValue());
    }

    @Test
    public void testWithIdAsStringIn() {
        BasicObject myObj1 = new BasicObject("xpto1");
        yawp.save(myObj1);

        BasicObject myObj2 = new BasicObject("xpto2");
        yawp.save(myObj2);

        List<BasicObject> objects = yawp(BasicObject.class)
                .where("id", "in", Arrays.asList(myObj1.getId().toString(), myObj2.getId().toString())).order("stringValue").list();
        assertEquals(2, objects.size());

        assertEquals("xpto1", objects.get(0).getStringValue());
        assertEquals("xpto2", objects.get(1).getStringValue());
    }

    @Test
    public void testWhereWithUnicode() {
        yawp.save(new BasicObject("\u00c1"));

        List<BasicObject> objects = yawp(BasicObject.class).where("stringValue", "=", "\u00c1").list();

        assertEquals(1, objects.size());
        assertEquals("\u00c1", objects.get(0).getStringValue());
    }

    @Test
    public void testChainedWheres() {
        saveManyBasicObjects(1);

        List<BasicObject> objects = yawp(BasicObject.class).where("intValue", "=", 1).where("stringValue", "=", "xpto").list();

        assertEquals(1, objects.size());
        assertEquals("xpto", objects.get(0).getStringValue());
        assertEquals(1, objects.get(0).getIntValue());
    }

    @Test
    public void testSimpleWhereWithNot() {
        saveManyBasicObjects(2);

        List<BasicObject> objects = yawp(BasicObject.class).where(c("intValue", "=", 1).not()).list();

        assertEquals(1, objects.size());
        assertEquals("xpto", objects.get(0).getStringValue());
        assertEquals(2, objects.get(0).getIntValue());
    }

    @Test
    public void testWhereWithOrNot() {
        saveManyBasicObjects(3);

        List<BasicObject> objects = yawp(BasicObject.class).where(or(c("intValue", "=", 1), c("intValue", "=", 2)).not()).list();

        assertEquals(1, objects.size());
        assertEquals("xpto", objects.get(0).getStringValue());
        assertEquals(3, objects.get(0).getIntValue());
    }

    @Test
    public void testWhereWithAndNot() {
        saveManyBasicObjects(5);

        List<BasicObject> objects = yawp(BasicObject.class).where(and(c("intValue", ">", 1), c("intValue", "<", 5)).not()).list();

        assertEquals(2, objects.size());
        sort(objects);
        assertEquals(1, objects.get(0).getIntValue());
        assertEquals(5, objects.get(1).getIntValue());
    }

    @Test
    public void testChainedWheresWithAnd() {
        saveManyBasicObjects(1);

        List<BasicObject> objects = yawp(BasicObject.class).where(and(c("intValue", "=", 1), c("stringValue", "=", "xpto"))).list();

        assertEquals(1, objects.size());
        assertEquals("xpto", objects.get(0).getStringValue());
        assertEquals(1, objects.get(0).getIntValue());
    }

    @Test
    public void testWhereWithOr() {
        saveManyBasicObjects(2);

        List<BasicObject> objects = yawp(BasicObject.class).where(or(c("intValue", "=", 1), c("intValue", "=", 2))).list();

        assertEquals(2, objects.size());
        sort(objects);

        assertEquals(2, objects.size());

        assertEquals("xpto", objects.get(0).getStringValue());
        assertEquals(1, objects.get(0).getIntValue());

        assertEquals("xpto", objects.get(1).getStringValue());
        assertEquals(2, objects.get(1).getIntValue());
    }

    private void sort(List<BasicObject> objects) {
        Collections.sort(objects, new Comparator<BasicObject>() {
            @Override
            public int compare(BasicObject o1, BasicObject o2) {
                return o1.getIntValue() - o2.getIntValue();
            }
        });
    }

    @Test
    public void testWhereWithComplexAndOrStructure() {
        saveManyBasicObjects(3);

        List<BasicObject> objects1 = yawp(BasicObject.class).where(
                or(and(c("intValue", "=", 1), c("intValue", "=", 2)), and(c("intValue", "=", 3), c("intValue", "=", 3)))).list();

        assertEquals(1, objects1.size());
        assertEquals(3, objects1.get(0).getIntValue());

        BaseCondition condition = or(and(c("intValue", "=", 3), c("intValue", "=", 3)), and(c("intValue", "=", 1), c("intValue", "=", 2)));
        List<BasicObject> objects2 = yawp(BasicObject.class).where(condition).list();

        assertEquals(1, objects2.size());
        assertEquals(3, objects2.get(0).getIntValue());
    }

    @Test
    public void testChainedWheresMultipleStatements() {
        saveManyBasicObjects(1);

        List<BasicObject> objects = yawp(BasicObject.class).where("intValue", "=", 1).where("stringValue", "=", "xpto").list();

        assertEquals(1, objects.size());
        assertEquals(1, objects.get(0).getIntValue());
        assertEquals("xpto", objects.get(0).getStringValue());
    }

    @Test
    public void testQueryFromOptions() {
        saveManyBasicObjects(3);

        QueryOptions options = QueryOptions
                .parse("{where: ['stringValue', '=', 'xpto'], order: [{p: 'intValue', d: 'desc'}], limit: 2}");

        List<BasicObject> objects = yawp(BasicObject.class).options(options).list();

        assertEquals(2, objects.size());
        assertEquals(3, objects.get(0).getIntValue());
        assertEquals(2, objects.get(1).getIntValue());
    }

    @Test
    public void testOrderWithUnicode() {
        yawp.save(new BasicObject("\u00e1"));
        yawp.save(new BasicObject("\u00e9"));
        yawp.save(new BasicObject("\u00ed"));

        List<BasicObject> objects = yawp(BasicObject.class).order("stringValue", "desc").list();

        assertEquals(3, objects.size());
        assertEquals("\u00ed", objects.get(0).getStringValue());
        assertEquals("\u00e9", objects.get(1).getStringValue());
        assertEquals("\u00e1", objects.get(2).getStringValue());
    }

    @Test
    public void testOrderWithTwoProperties() {
        saveManyBasicObjects(2, "xpto1");
        saveManyBasicObjects(2, "xpto2");

        List<BasicObject> objects = yawp(BasicObject.class).order("stringValue", "desc").order("intValue", "desc").list();

        assertEquals(4, objects.size());

        assertEquals("xpto2", objects.get(0).getStringValue());
        assertEquals("xpto2", objects.get(1).getStringValue());
        assertEquals("xpto1", objects.get(2).getStringValue());
        assertEquals("xpto1", objects.get(3).getStringValue());

        assertEquals(2, objects.get(0).getIntValue());
        assertEquals(1, objects.get(1).getIntValue());
        assertEquals(2, objects.get(2).getIntValue());
        assertEquals(1, objects.get(3).getIntValue());
    }

    @Test
    public void testSortWithTwoProperties() {
        saveManyBasicObjects(2, "xpto1");
        saveManyBasicObjects(2, "xpto2");

        List<BasicObject> objects = yawp(BasicObject.class).sort("stringValue", "desc").sort("intValue", "desc").list();

        assertEquals(4, objects.size());

        assertEquals("xpto2", objects.get(0).getStringValue());
        assertEquals("xpto2", objects.get(1).getStringValue());
        assertEquals("xpto1", objects.get(2).getStringValue());
        assertEquals("xpto1", objects.get(3).getStringValue());

        assertEquals(2, objects.get(0).getIntValue());
        assertEquals(1, objects.get(1).getIntValue());
        assertEquals(2, objects.get(2).getIntValue());
        assertEquals(1, objects.get(3).getIntValue());
    }

    @Test
    public void testLimit() {
        saveManyBasicObjects(3);

        List<BasicObject> objects = yawp(BasicObject.class).order("intValue", "desc").limit(1).list();

        assertEquals(1, objects.size());
        assertEquals(3, objects.get(0).getIntValue());
    }

    @Test
    public void testCursor() {
        saveManyBasicObjects(3);

        QueryBuilder<BasicObject> q = yawp(BasicObject.class).order("intValue", "desc").limit(1);

        List<BasicObject> objects1 = q.list();
        assertEquals(3, objects1.get(0).getIntValue());

        List<BasicObject> objects2 = q.list();
        assertEquals(2, objects2.get(0).getIntValue());

        List<BasicObject> objects3 = yawp(BasicObject.class).cursor(q.getCursor()).order("intValue", "desc").limit(1).list();
        assertEquals(1, objects3.get(0).getIntValue());
    }

    @Test
    public void testFindByIdUsingWhere() {
        BasicObject object = new BasicObject("xpto");

        yawp.save(object);

        BasicObject retrievedObject = yawp(BasicObject.class).where("id", "=", object.getId()).first();
        assertEquals("xpto", retrievedObject.getStringValue());
    }

    @Test
    public void testFindByIdUsingWhereIn() {
        BasicObject object1 = new BasicObject("xpto1");
        yawp.save(object1);

        BasicObject object2 = new BasicObject("xpto2");
        yawp.save(object2);

        final List<IdRef<BasicObject>> ids = Arrays.asList(object1.getId(), object2.getId());
        List<BasicObject> objects = yawp(BasicObject.class).where("id", "in", ids).order("stringValue").list();
        assertEquals(2, objects.size());
        assertEquals("xpto1", objects.get(0).getStringValue());
        assertEquals("xpto2", objects.get(1).getStringValue());
    }

    @Test
    public void testWhereInWithEmptyList() {
        saveManyBasicObjects(1);

        List<BasicObject> objects = yawp(BasicObject.class).where("intValue", "in", Collections.emptyList()).list();

        assertEquals(0, objects.size());
    }

    @Test
    public void testWhereInWithNullList() {
        saveManyBasicObjects(1);

        List<BasicObject> objects = yawp(BasicObject.class).where("intValue", "in", null).list();

        assertEquals(0, objects.size());
    }

    @Test
    public void testWhereInWithEmptyListOrTrueExpression() {
        saveManyBasicObjects(3);
        BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
        BaseCondition condition = or(emptyListCondition, c("stringValue", "=", "xpto"));

        List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
        assertEquals(3, objects.size());
    }

    @Test
    public void testWhereInWithEmptyListOrFalseExpression() {
        saveManyBasicObjects(1);
        BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
        BaseCondition condition = or(emptyListCondition, c("stringValue", "=", "otpx"));

        List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
        assertEquals(0, objects.size());
    }

    @Test
    public void testWhereInWithEmptyListAndTrueExpression() {
        saveManyBasicObjects(1);
        BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
        BaseCondition condition = and(emptyListCondition, c("stringValue", "=", "xpto"));

        List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
        assertEquals(0, objects.size());
    }

    @Test
    public void testWhereInWithEmptyListAndFalseExpression() {
        saveManyBasicObjects(1);
        BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
        BaseCondition condition = and(emptyListCondition, c("stringValue", "=", "otpx"));

        List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
        assertEquals(0, objects.size());
    }

    @Test
    public void testWhereInWithEmptyListOrComplexExpression() {
        saveManyBasicObjects(1);
        BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
        BaseCondition condition = and(emptyListCondition, and(c("stringValue", "=", "otpx"), emptyListCondition));

        List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
        assertEquals(0, objects.size());
    }

    @Test
    public void testIds() {
        saveManyBasicObjects(3);
        yawp.save(new BasicObject("different"));

        List<IdRef<BasicObject>> objects = yawp(BasicObject.class).where("stringValue", "=", "xpto").ids();
        assertEquals(3, objects.size());
    }

    @Test
    public void testOnlyId() {
        Long firstId = saveOneObject("xpto", 10).getId().asLong();

        IdRef<BasicObject> id = yawp(BasicObject.class).where("stringValue", "=", "xpto").onlyId();
        assertEquals(firstId, id.asLong());
    }

    @Test
    public void testOnlyIdNoResult() {
        try {
            yawp(BasicObject.class).where("stringValue", "=", "xpto").onlyId();
        } catch (NoResultException ex) {
            return;
        }
        assertTrue(false);
    }

    @Test
    public void testOnlyIdMoreThanOneResult() {
        saveManyBasicObjects(2);
        try {
            yawp(BasicObject.class).where("stringValue", "=", "xpto").onlyId();
        } catch (MoreThanOneResultException ex) {
            return;
        }
        assertTrue(false);
    }

    @Test
    public void testWhereWithoutIndex() {
        yawp.save(new BasicObject("a", 1l));
        yawp.save(new BasicObject("b", 2l));

        List<BasicObject> objects = yawp(BasicObject.class).where("longValue", "=", 1l).list();
        assertObjects(objects, "a");
    }

    @Test
    public void testWhereWithoutIndexAndCondition() {
        yawp.save(new BasicObject("a", 1l));
        yawp.save(new BasicObject("a", 2l));
        yawp.save(new BasicObject("b", 1l));

        List<BasicObject> objects = yawp(BasicObject.class).where(c("stringValue", "=", "a").and(c("longValue", "=", 1l))).list();
        assertObjects(objects, "a");
    }

    @Test
    public void testWhereWithoutIndexOrCondition() {
        yawp.save(new BasicObject("a", 1l));
        yawp.save(new BasicObject("b", 2l));
        yawp.save(new BasicObject("c", 1l));

        List<BasicObject> objects = yawp(BasicObject.class).where(c("stringValue", "=", "a").or(c("longValue", "=", 1l))).list();
        assertObjects(objects, "a", "c");
    }

    @Test
    public void testWhereWithoutIndexComplexConditions() {
        yawp.save(new BasicObject("a", 1l));
        yawp.save(new BasicObject("a", 2l));
        yawp.save(new BasicObject("c", 3l));
        yawp.save(new BasicObject("c", 4l));
        yawp.save(new BasicObject("d", 5l));
        yawp.save(new BasicObject("e", 6l));

        BaseCondition c2 = c("stringValue", "=", "c").and(c("longValue", "=", 3l));
        List<BasicObject> objects = yawp(BasicObject.class).where(c("stringValue", "=", "a").or(c2)).list();
        assertObjects(objects, "a", "a", "c");
    }

    @Test
    public void testWhereWithoutIndexWithId() {
        yawp.save(setId(new BasicObject("a", 1l), 1l));
        yawp.save(setId(new BasicObject("b", 2l), 2l));
        yawp.save(setId(new BasicObject("c", 3l), 3l));

        IdRef<BasicObject> id = IdRef.create(yawp, BasicObject.class, 1l);

        List<BasicObject> objects = yawp(BasicObject.class).where(c("id", "=", id).or(c("longValue", "=", 3l))).list();
        assertObjects(objects, "a", "c");

        objects = yawp(BasicObject.class).where(c("id", "=", id).and(c("longValue", "=", 1l))).list();
        assertObjects(objects, "a");

        objects = yawp(BasicObject.class).where(c("id", "=", id).and(c("longValue", "=", 3l))).list();
        assertObjects(objects);
    }

    @Test
    public void testQueryRef() {
        BasicObject ref1 = new BasicObject("right");
        BasicObject ref2 = new BasicObject("wrong");

        yawp.save(ref1);
        yawp.save(ref2);

        yawp.save(new BasicObject("a", ref1.getId()));
        yawp.save(new BasicObject("b", ref2.getId()));

        BasicObject object = yawp(BasicObject.class).where("objectId->stringValue", "=", "right").only();
        assertEquals("a", object.getStringValue());
    }

    @Test
    public void testQueryRefTwoLevels() {
        BasicObject ref1_1 = new BasicObject("right");
        BasicObject ref2_1 = new BasicObject("wrong");

        yawp.save(ref1_1);
        yawp.save(ref2_1);

        BasicObject ref1 = new BasicObject("x", ref1_1.getId());
        BasicObject ref2 = new BasicObject("y", ref2_1.getId());

        yawp.save(ref1);
        yawp.save(ref2);

        yawp.save(new BasicObject("a", ref1.getId()));
        yawp.save(new BasicObject("b", ref2.getId()));

        BasicObject object = yawp(BasicObject.class).where("objectId->objectId->stringValue", "=", "right").only();
        assertEquals("a", object.getStringValue());
        assertEquals("x", object.getObjectId().fetch().getStringValue());
    }

    @Test
    public void testQueryRefWithPreFilter() {
        BasicObject ref1 = new BasicObject("right");
        BasicObject ref2 = new BasicObject("right");

        yawp.save(ref1);
        yawp.save(ref2);

        yawp.save(new BasicObject("a", ref1.getId()));
        yawp.save(new BasicObject("b", ref2.getId()));

        BasicObject object = yawp(BasicObject.class).where(c("objectId->stringValue", "=", "right").and(c("stringValue", "=", "a"))).only();
        assertEquals("a", object.getStringValue());
    }

    @Test
    public void testQueryParentRef() {
        Parent parent1 = new Parent("right");
        Parent parent2 = new Parent("wrong");

        yawp.save(parent1);
        yawp.save(parent2);

        Child child1 = new Child("x", parent1.getId());
        Child child2 = new Child("y", parent2.getId());

        yawp.save(child1);
        yawp.save(child2);

        yawp.save(new Grandchild("a", child1.getId()));
        yawp.save(new Grandchild("b", child2.getId()));

        Grandchild object = yawp(Grandchild.class).where("childId->parentId->name", "=", "right").only();
        assertEquals("a", object.getName());

        object = yawp(Grandchild.class).where("parent->parent->name", "=", "right").only();
        assertEquals("a", object.getName());
    }

    @Test
    public void testHierarchySuperClassFieldQuery() {
        ObjectSubClass child = new ObjectSubClass("xpto");
        yawp.save(child);

        ObjectSubClass retrievedObject = yawp(ObjectSubClass.class).where("name", "=", "xpto").only();
        assertEquals("xpto", retrievedObject.getName());
    }

    @Test(expected = RuntimeException.class)
    public void testIdsWithPostFilter() {
        assertEquals(0, yawp(BasicObject.class).where("longValue", "=", 2l).ids().size());
    }

    @Test(expected = RuntimeException.class)
    public void testIdsWithPostOrder() {
        assertEquals(0, yawp(BasicObject.class).sort("longValue").ids().size());
    }

    private void assertObjects(List<BasicObject> objects, String... strings) {
        assertEquals(strings.length, objects.size());
        for (int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], objects.get(i).getStringValue());
        }
    }
}
