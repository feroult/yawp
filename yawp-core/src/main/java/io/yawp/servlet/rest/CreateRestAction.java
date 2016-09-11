package io.yawp.servlet.rest;

import io.yawp.repository.FutureObject;

import java.util.ArrayList;
import java.util.List;

public class CreateRestAction extends RestAction {

    public CreateRestAction() {
        super("create");
    }

    @Override
    public void shield() {
        shield.protectCreate();
    }

    @Override
    public Object action() {
        if (isRequestBodyJsonArray()) {
            return createFromArray(getObjects());
        }

        return createFromObject(getObject());
    }

    private Object createFromObject(Object object) {
        return saveObject(object);
    }

    private Object createFromArray(List<?> objects) {
        return saveObjecs(objects);
    }

    private Object saveObjecs(List<?> objects) {
        List<FutureObject<Object>> futures = new ArrayList<>();
        List<Object> resultObjects = new ArrayList<>();

        for (Object object : objects) {
            futures.add(saveObjectAsync(object));
        }

        for (FutureObject<Object> future : futures) {
            Object object = transform(future.get());
            applyGetFacade(object);
            resultObjects.add(object);
        }

        return resultObjects;
    }

    protected Object saveObject(Object object) {
        save(object);
        applyGetFacade(object);
        return transform(object);
    }

    protected FutureObject<Object> saveObjectAsync(Object object) {
        return saveAsync(object);
    }

}
