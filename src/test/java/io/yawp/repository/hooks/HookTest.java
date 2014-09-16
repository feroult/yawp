package io.yawp.repository.hooks;

import static org.junit.Assert.assertEquals;
import io.yawp.repository.models.basic.HookedObject;
import io.yawp.utils.EndpointTestCase;

import java.util.List;

import org.junit.Test;

public class HookTest extends EndpointTestCase {

	@Test
	public void testBeforeSave() {
		HookedObject object = new HookedObject("before_save");

		r.saveWithHooks(object);
		assertEquals("xpto before save", object.getStringValue());

		HookedObject retrievedObject = object.getId().fetch();
		assertEquals("xpto before save", retrievedObject.getStringValue());
	}

	@Test
	public void testAfterSave() {
		HookedObject object = new HookedObject("after_save");
		r.saveWithHooks(object);
		assertEquals("xpto after save", object.getStringValue());
	}

	@Test
	public void testAllObjectsHook() {
		HookedObject object = new HookedObject("all_objects");
		r.saveWithHooks(object);
		assertEquals("xpto all objects", object.getStringValue());
	}

	@Test
	public void testBeforeQuery() {
		r.save(new HookedObject("xpto1"));
		r.save(new HookedObject("xpto2"));

		List<HookedObject> objects = r.queryWithHooks(HookedObject.class).list();

		assertEquals(1, objects.size());
		assertEquals("xpto1", objects.get(0).getStringValue());
	}

}
