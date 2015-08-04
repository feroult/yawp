package io.yawp.repository.hooks;

import static org.junit.Assert.assertEquals;
import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.DeletedHookedObject;
import io.yawp.repository.models.basic.HookedObject;

import java.util.List;

import org.junit.Test;

public class HookTest extends EndpointTestCase {

	@Test
	public void testBeforeSave() {
		HookedObject object = new HookedObject("before_save");

		yawp.saveWithHooks(object);
		assertEquals("xpto before save", object.getStringValue());

		HookedObject retrievedObject = object.getId().fetch();
		assertEquals("xpto before save", retrievedObject.getStringValue());
	}

	@Test
	public void testAfterSave() {
		HookedObject object = new HookedObject("after_save");
		yawp.saveWithHooks(object);
		assertEquals("xpto after save", object.getStringValue());
	}

	@Test
	public void testAllObjectsHook() {
		HookedObject object = new HookedObject("all_objects");
		yawp.saveWithHooks(object);
		assertEquals("xpto all objects", object.getStringValue());
	}

	@Test
	public void testBeforeQuery() {
		yawp.save(new HookedObject("xpto1"));
		yawp.save(new HookedObject("xpto2"));

		List<HookedObject> objects = yawpWithHooks(HookedObject.class).list();

		assertEquals(1, objects.size());
		assertEquals("xpto1", objects.get(0).getStringValue());
	}
	
	@Test
	public void testBeforeDestroy() {
		HookedObject hookObject = new HookedObject("xpto1");
		yawp.save(hookObject);
		HookedObject xpto = yawp(HookedObject.class).first();
		
		yawp.destroy(xpto.getId());
		
		DeletedHookedObject deletedObject = yawp(DeletedHookedObject.class).only();
		assertEquals(hookObject.getStringValue(), deletedObject.getStringValue());
	}

}
