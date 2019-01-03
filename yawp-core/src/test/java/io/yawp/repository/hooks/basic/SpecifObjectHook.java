package io.yawp.repository.hooks.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.hooks.*;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.HookedObject;
import io.yawp.repository.query.QueryBuilder;

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
    public void beforeQuery(BeforeQueryObject<HookedObject> obj) {
        if (HookTest.LoggedUser.filter == null) {
            return;
        }
        obj.getQuery().where("stringValue", "=", HookTest.LoggedUser.filter);
    }

    @Override
    public void beforeDestroy(IdRef<HookedObject> id) {
        HookedObject object = id.fetch();
        yawp.save(new BasicObject(object.getStringValue() + ": " + id));
    }

    @Override
    public void afterDestroy(IdRef<HookedObject> id) {
        yawp.save(new BasicObject("afterDestroy test: " + id));
    }

    @Override
    public void afterQuery(AfterQueryListObject<HookedObject> obj) {
        HookTest.AfterQueryTest.msgs.add("list:" + obj.getList().size());
    }

    @Override
    public void afterQuery(AfterQueryIdsObject<HookedObject> obj) {
        HookTest.AfterQueryTest.msgs.add("ids:" + obj.getIds().size());
    }

    @Override
    public void afterQuery(AfterQueryFetchObject<HookedObject> obj) {
        HookTest.AfterQueryTest.msgs.add("fetch:" + obj.getElement().getId());
    }

    private boolean isBeforeSaveTest(HookedObject object) {
        return object.getStringValue() != null && object.getStringValue().equals("before_save");
    }

    private boolean isAfterSaveTest(HookedObject object) {
        return object.getStringValue() != null && object.getStringValue().equals("after_save");
    }
}
