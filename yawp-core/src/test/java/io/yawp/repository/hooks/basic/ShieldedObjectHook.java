package io.yawp.repository.hooks.basic;

import io.yawp.repository.hooks.Hook;
import io.yawp.repository.models.basic.ShieldedObject;

public class ShieldedObjectHook extends Hook<ShieldedObject> {

    @Override
    public void beforeShield(ShieldedObject object) {
        if(itNeedsToApplyBeforeShield(object)) {
            object.setStringValue("applied beforeShield");
        }
    }

    private boolean itNeedsToApplyBeforeShield(ShieldedObject object) {
        return object.getStringValue() != null && object.getStringValue().equals("apply beforeShield");
    }
}
