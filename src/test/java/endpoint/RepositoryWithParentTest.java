package endpoint;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class RepositoryWithParentTest extends EndpointTestCase {

	@Test
	public void testSaveWithParent() {
		ObjectWithIdRef object1 = new ObjectWithIdRef("xpto1");
		ObjectWithIdRef object2 = new ObjectWithIdRef("xpto2");
		r.save(object1);
		r.save(object2);

		AnotherObjectWithIdRef anotherObject1 = new AnotherObjectWithIdRef(object1.getId(), "xpto3");
		r.save(anotherObject1);
		AnotherObjectWithIdRef anotherObject2 = new AnotherObjectWithIdRef(object2.getId(), "xpto4");
		r.save(anotherObject2);

		AnotherObjectWithIdRef loadedAnotherObject = r.query(AnotherObjectWithIdRef.class).from(object1.getId()).only();
		assertEquals(anotherObject1.getId(), loadedAnotherObject.getId());
		assertEquals(anotherObject1.getText(), loadedAnotherObject.getText());
		assertEquals(object1.getId(), loadedAnotherObject.getObjectWithIdRefId());
		assertEquals(object1.getText(), loadedAnotherObject.getObjectWithIdRefId().fetch().getText());
	}

	@Test
	public void testSaveWithParentMultipleChildren() {
		ObjectWithIdRef object1 = new ObjectWithIdRef("xpto1");
		ObjectWithIdRef object2 = new ObjectWithIdRef("xpto2");
		r.save(object1);
		r.save(object2);

		AnotherObjectWithIdRef[] anotherObjects = new AnotherObjectWithIdRef[3];
		for (int i = 0; i < anotherObjects.length; i++) {
			anotherObjects[i] = new AnotherObjectWithIdRef(object1.getId(), "xpto3");
			r.save(anotherObjects[i]);
		}
		AnotherObjectWithIdRef anotherObject2 = new AnotherObjectWithIdRef(object2.getId(), "xpto4");
		r.save(anotherObject2);

		List<AnotherObjectWithIdRef> loadedObjects = r.query(AnotherObjectWithIdRef.class).from(object1.getId()).list();
		assertEquals(3, loadedObjects.size());
		for (AnotherObjectWithIdRef obj : loadedObjects) {
			assertEquals(object1.getId(), obj.getObjectWithIdRefId());
			assertEquals(object1.getText(), obj.getObjectWithIdRefId().fetch().getText());
		}
	}

	@Test
	public void testSaveWithGrandParent() {
		ObjectWithIdRef object1 = new ObjectWithIdRef("xpto1");
		r.save(object1);
		ObjectWithIdRef object2 = new ObjectWithIdRef("xpto2");
		r.save(object2);

		AnotherObjectWithIdRef anotherObject1 = new AnotherObjectWithIdRef(object1.getId(), "xpto3");
		r.save(anotherObject1);
		AnotherObjectWithIdRef anotherObject2 = new AnotherObjectWithIdRef(object2.getId(), "xpto4");
		r.save(anotherObject2);
		
		GrandChildObjectWithIdRef grandChild = new GrandChildObjectWithIdRef(anotherObject1.getId(), "xpto5");
		r.save(grandChild);

		AnotherObjectWithIdRef loadedAnotherObject = r.query(AnotherObjectWithIdRef.class).from(object1.getId()).only();
		GrandChildObjectWithIdRef loadedGrandChild = r.query(GrandChildObjectWithIdRef.class).from(loadedAnotherObject.getId()).only();

		assertEquals(grandChild.getId(), loadedGrandChild.getId());
		assertEquals(grandChild.getText(), loadedGrandChild.getText());
		assertEquals(anotherObject1.getId(), loadedGrandChild.getAnotherObjectWithIdRefId());
		assertEquals(anotherObject1.getText(), loadedGrandChild.getAnotherObjectWithIdRefId().fetch().getText());
		assertEquals(object1.getId(), loadedGrandChild.getAnotherObjectWithIdRefId().fetch().getObjectWithIdRefId());
		assertEquals(object1.getText(), loadedGrandChild.getAnotherObjectWithIdRefId().fetch().getObjectWithIdRefId().fetch().getText());
	}

	private static class GrandGrandChild {
		@Id IdRef<GrandGrandChild> id;
		String text;
		@Parent IdRef<GrandChildObjectWithIdRef> parent;
		
		@SuppressWarnings("unused")
		private GrandGrandChild() {}
		
		GrandGrandChild(String text, IdRef<GrandChildObjectWithIdRef> parent) {
			this.text = text;
			this.parent = parent;
		}
	}
	
	@Test
	public void testSaveWithGrandGrandParentAndInnerClass() {
		ObjectWithIdRef object1 = new ObjectWithIdRef("xpto1");
		r.save(object1);
		AnotherObjectWithIdRef anotherObject1 = new AnotherObjectWithIdRef(object1.getId(), "xpto2");
		r.save(anotherObject1);
		GrandChildObjectWithIdRef grandChild = new GrandChildObjectWithIdRef(anotherObject1.getId(), "xpto3");
		r.save(grandChild);
		GrandGrandChild grandGrandChild = new GrandGrandChild("xpto4", grandChild.getId());
		r.save(grandGrandChild);

		AnotherObjectWithIdRef loadedAnotherObject = r.query(AnotherObjectWithIdRef.class).from(object1.getId()).only();
		GrandChildObjectWithIdRef loadedGrandChild = r.query(GrandChildObjectWithIdRef.class).from(loadedAnotherObject.getId()).only();
		GrandGrandChild loadedGrandGrandChild = r.query(GrandGrandChild.class).from(loadedGrandChild.getId()).only();

		assertEquals(grandGrandChild.id, loadedGrandGrandChild.id);
		assertEquals(grandGrandChild.text, loadedGrandGrandChild.text);
		assertEquals(grandGrandChild.parent, loadedGrandGrandChild.parent);
		assertEquals(grandChild.getId(), loadedGrandChild.getId());
		assertEquals(grandChild.getText(), loadedGrandChild.getText());
		assertEquals(anotherObject1.getId(), loadedGrandChild.getAnotherObjectWithIdRefId());
		assertEquals(anotherObject1.getText(), loadedGrandChild.getAnotherObjectWithIdRefId().fetch().getText());
		assertEquals(object1.getId(), loadedGrandChild.getAnotherObjectWithIdRefId().fetch().getObjectWithIdRefId());
		assertEquals(object1.getText(), loadedGrandChild.getAnotherObjectWithIdRefId().fetch().getObjectWithIdRefId().fetch().getText());
	}
}
