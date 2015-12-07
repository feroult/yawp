package io.yawp.driver.postgresql.datastore;

import static io.yawp.repository.query.condition.Condition.and;
import static io.yawp.repository.query.condition.Condition.c;
import static org.junit.Assert.assertEquals;

import io.yawp.commons.utils.Environment;
import io.yawp.driver.postgresql.IdRefToKey;
import io.yawp.driver.postgresql.datastore.models.Child;
import io.yawp.driver.postgresql.datastore.models.Grandchild;
import io.yawp.driver.postgresql.datastore.models.Parent;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.driver.postgresql.tools.DatabaseSynchronizer;
import io.yawp.repository.ObjectModel;
import io.yawp.repository.query.QueryBuilder;

import java.util.Arrays;
import java.util.List;

import org.junit.*;

public class DatastoreTest extends DatastoreTestCase {

    private Datastore datastore;

    @BeforeClass
    public static void setUpClass() {
        configureEnvironment();
        createDatabase();
    }

    @AfterClass
    public static void tearDownTestCase() {
        InitialContextSetup.unregister();
    }

    private static void configureEnvironment() {
        Environment.set("test");
        InitialContextSetup.configure("configuration/jetty-env-test.xml");
    }

    private static void createDatabase() {
        DatabaseSynchronizer dbSynchronizer = new DatabaseSynchronizer();
        dbSynchronizer.sync(yawp.getFeatures().getEndpointClazzes());
    }

    @Before
    public void before() {
        datastore = Datastore.create(new ConnectionManager());
        truncate();
    }

    @After
    public void after() {
        // truncate();
    }

    private void truncate() {
        connectionManager.execute("truncate table parents;");
    }

    @Test
    public void testCreateRetrieveEntity() throws EntityNotFoundException {
        Entity entity = new Entity("parents");
        entity.setProperty("name", "jim");

        datastore.put(entity);

        Entity retrievedEntity = datastore.get(entity.getKey());
        assertEquals("jim", retrievedEntity.getProperty("name"));
    }

    @Test
    public void testCreateUpdateEntity() throws EntityNotFoundException {
        Entity entity = new Entity("parents");
        entity.setProperty("name", "jim");

        Key key = datastore.put(entity);

        entity.setProperty("name", "robert");
        datastore.put(entity);

        Entity retrievedEntity = datastore.get(key);
        assertEquals("robert", retrievedEntity.getProperty("name"));

    }

    @Test(expected = EntityNotFoundException.class)
    public void delete() throws EntityNotFoundException {
        Key key = KeyFactory.createKey("parents", "xpto");
        Entity entity = new Entity(key);
        datastore.put(entity);

        datastore.delete(key);

        datastore.get(key);
    }

    @Test
    public void testForceName() throws EntityNotFoundException {
        Key key = KeyFactory.createKey("parents", "xpto");

        Entity entity = new Entity(key);
        entity.setProperty("name", "jim");

        datastore.put(entity);

        Entity retrievedEntity = datastore.get(key);
        assertEquals("jim", retrievedEntity.getProperty("name"));
    }

    @Test
    public void testForceId() throws EntityNotFoundException {
        Key key = KeyFactory.createKey("parents", 123l);

        Entity entity = new Entity(key);
        entity.setProperty("name", "jim");

        datastore.put(entity);

        Entity retrievedEntity = datastore.get(key);
        assertEquals("jim", retrievedEntity.getProperty("name"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testChildKey() throws EntityNotFoundException {
        Key parentKey = KeyFactory.createKey("parents", 1l);
        Key childKey = KeyFactory.createKey(parentKey, "parents", 1l);

        Entity entity = new Entity(childKey);
        entity.setProperty("name", "jim");

        datastore.put(entity);

        Entity retrievedEntity = datastore.get(childKey);
        assertEquals("jim", retrievedEntity.getProperty("name"));

        Key anotherParentKey = KeyFactory.createKey("parents", 2l);
        Key anotherChildKey = KeyFactory.createKey(anotherParentKey, "parents", 1l);

        datastore.get(anotherChildKey);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGrandchildKey() throws EntityNotFoundException {
        Key parentKey = KeyFactory.createKey("parents", 1l);
        Key childKey = KeyFactory.createKey(parentKey, "children", 1l);
        Key grandchildKey = KeyFactory.createKey(childKey, "parents", 1l);

        Entity entity = new Entity(grandchildKey);
        entity.setProperty("name", "jim");

        datastore.put(entity);

        Entity retrievedEntity = datastore.get(grandchildKey);
        assertEquals("jim", retrievedEntity.getProperty("name"));

        Key anotherParentKey = KeyFactory.createKey("parents", 2l);
        Key anotherChildKey = KeyFactory.createKey(anotherParentKey, "children", 1l);
        Key anotherGrandchildKey = KeyFactory.createKey(anotherChildKey, "parents", 1l);

        datastore.get(anotherGrandchildKey);
    }

    @Test
    public void testSimpleQuery() throws FalsePredicateException {
        saveParentWithName("jim");
        saveParentWithName("robert");

        QueryBuilder<Parent> builder = QueryBuilder.q(Parent.class, yawp);
        builder.where(c("name", "=", "jim"));

        List<Entity> entities = datastore.query(new Query(builder, false));

        assertEquals(1, entities.size());
        assertEquals("jim", entities.get(0).getProperty("name"));
    }

    @Test
    public void testJoinedQuery() throws FalsePredicateException {
        saveParentWithName("jim");
        saveParentWithName("robert");

        QueryBuilder<Parent> builder = QueryBuilder.q(Parent.class, yawp);
        builder.where(and(c("name", "=", "jim"), c("name", ">", "j")));

        List<Entity> entities = datastore.query(new Query(builder, false));

        assertEquals(1, entities.size());
        assertEquals("jim", entities.get(0).getProperty("name"));
    }

    @Test
    public void testQueryOrder() throws FalsePredicateException {
        saveParentWithName("jim");
        saveParentWithName("robert");

        QueryBuilder<Parent> builder = QueryBuilder.q(Parent.class, yawp);
        builder.order("name", "desc");

        List<Entity> entities = datastore.query(new Query(builder, false));

        assertEquals(2, entities.size());
        assertEquals("robert", entities.get(0).getProperty("name"));
        assertEquals("jim", entities.get(1).getProperty("name"));
    }

    @Test
    public void testQueryLimit() throws FalsePredicateException {
        saveParentWithName("jim");
        saveParentWithName("robert");

        QueryBuilder<Parent> builder = QueryBuilder.q(Parent.class, yawp);
        builder.order("name", "desc");
        builder.limit(1);

        List<Entity> entities = datastore.query(new Query(builder, false));

        assertEquals(1, entities.size());
        assertEquals("robert", entities.get(0).getProperty("name"));
    }

    @Test
    public void testQueryParent() throws FalsePredicateException {
        saveParentWithName("robert");

        Key parentKey = KeyFactory.createKey("parents", 1l);
        saveWithParentAndName(parentKey, "children", "jim");

        assertJimIsFromAncestor(Child.class, Parent.class, parentKey);
    }

    @Test
    public void testQueryGrandchild() throws FalsePredicateException {
        saveParentWithName("robert");

        Key parentKey = KeyFactory.createKey("parents", 1l);
        Key childKey = KeyFactory.createKey(parentKey, "children", 10l);

        saveWithParentAndName(childKey, "grandchildren", "jim");

        assertJimIsFromAncestor(Grandchild.class, Child.class, childKey);
        assertJimIsFromAncestor(Grandchild.class, Parent.class, parentKey);
    }

    @Test
    public void testQueryIn() throws FalsePredicateException {
        Entity entity = new Entity("parents");
        entity.setProperty("name", "jim");
        entity.setProperty("__name", "jim");
        entity.setProperty("age", 27);
        datastore.put(entity);

        assertQueryInForField("age", Arrays.asList(10, 27));
        assertQueryInForField("name", Arrays.asList("jim", "john"));
    }

    @Test
    public void testQueryInWithKey() throws FalsePredicateException {
        ObjectModel model = new ObjectModel(Parent.class);

        Key key = KeyFactory.createKey("parents", 1l);
        Entity entity = new Entity(key);
        entity.setProperty("name", "jim");
        entity.setProperty("__name", "jim");
        entity.setProperty("age", 27);
        datastore.put(entity);

        Key anotherKey = KeyFactory.createKey("parents", 2l);

        assertQueryInForField("id", Arrays.asList(IdRefToKey.toIdRef(yawp, key, model), IdRefToKey.toIdRef(yawp, anotherKey, model)));
    }

    private void assertQueryInForField(String field, List<?> list) throws FalsePredicateException {
        QueryBuilder<Parent> builder = QueryBuilder.q(Parent.class, yawp);
        builder.where(c(field, "in", list));

        List<Entity> entities = datastore.query(new Query(builder, false));

        assertEquals(1, entities.size());
        assertEquals(27.0, entities.get(0).getProperty("age"));
    }

    private <T> void assertJimIsFromAncestor(Class<T> clazz, Class<?> ancestorClazz, Key parentKey) throws FalsePredicateException {
        QueryBuilder<T> builder = QueryBuilder.q(clazz, yawp);
        builder.from(IdRefToKey.toIdRef(yawp, parentKey, new ObjectModel(ancestorClazz)));

        List<Entity> entities = datastore.query(new Query(builder, false));
        assertEquals(1, entities.size());
        assertEquals("jim", entities.get(0).getProperty("name"));
    }

    private Key saveWithParentAndName(Key parentKey, String kind, String name) {
        Key childKey = KeyFactory.createKey(parentKey, kind, 100l);

        Entity entity = new Entity(childKey);
        entity.setProperty("name", name);
        entity.setProperty("__name", name);

        datastore.put(entity);
        return childKey;
    }

    private void saveParentWithName(String name) {
        Entity entity = new Entity("parents");
        entity.setProperty("name", name);
        entity.setProperty("__name", name);
        datastore.put(entity);
    }
}
