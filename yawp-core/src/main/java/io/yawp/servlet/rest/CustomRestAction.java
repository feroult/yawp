package io.yawp.servlet.rest;

import io.yawp.commons.http.JsonResponse;

public class CustomRestAction extends RestAction {

    public CustomRestAction() {
        super("custom");
    }

    @Override
    public void shield() {
        shield.protectCustom();
    }

    @Override
    public Object action() {
        Object object = r.action(id, endpointClazz, customActionKey, requestJson, params);

        if (object == null) {
            return new JsonResponse("");
        }

        applyGetFacade(object);
        if (hasTransformer()) {
            return transform(object);
        }

        return object;
    }
}
