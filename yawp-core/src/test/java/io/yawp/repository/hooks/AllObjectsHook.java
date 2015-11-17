package io.yawp.repository.hooks;

import io.yawp.repository.models.basic.HookedObject;

public class AllObjectsHook extends Hook<Object> {

    @Override
    public void afterSave(Object object) {
        if (!isHookTest(object)) {
            return;
        }

        HookedObject hooked = (HookedObject) object;
        hooked.setStringValue("xpto all objects");
    }

    private boolean isHookTest(Object object) {
        if (!HookedObject.class.isInstance(object)) {
            return false;
        }
        HookedObject hooked = (HookedObject) object;
        return hooked.getStringValue() != null && hooked.getStringValue().equals("all_objects");
    }
}
