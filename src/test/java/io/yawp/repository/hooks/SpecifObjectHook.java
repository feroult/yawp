package io.yawp.repository.hooks;

import io.yawp.repository.models.basic.HookedObject;
import io.yawp.repository.query.DatastoreQuery;

public class SpecifObjectHook extends Hook<HookedObject> {

	@Override
	public void beforeSave(HookedObject object) {
		if (!isBeforeSaveTest(object)) {
			return;
		}
		object.setStringValue("xpto before save");
	}

	@Override
	public void afterSave(HookedObject object) {
		if (!isAfterSaveTest(object)) {
			return;
		}
		object.setStringValue("xpto after save");
	}

	@Override
	public void beforeQuery(DatastoreQuery<HookedObject> q) {
		q.where("stringValue", "=", "xpto1");
	}

	private boolean isBeforeSaveTest(HookedObject object) {
		return object.getStringValue() != null && object.getStringValue().equals("before_save");
	}

	private boolean isAfterSaveTest(HookedObject object) {
		return object.getStringValue() != null && object.getStringValue().equals("after_save");
	}
}
