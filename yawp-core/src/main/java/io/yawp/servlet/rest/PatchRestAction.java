package io.yawp.servlet.rest;

import io.yawp.commons.utils.FacadeUtils;
import io.yawp.commons.utils.JsonUtils;

public class PatchRestAction extends UpdateRestAction {

    @Override
    protected void beforeShieldHooks() {
        assert !isRequestBodyJsonArray();
        FacadeUtils.copyOtherProperties(id.fetch(), getObject(), JsonUtils.getProperties(requestJson));
    }

}
