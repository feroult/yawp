package io.yawp.repository.shields;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.models.parents.Parent;

public class ParentShield extends AbstractShield<Parent> {

    @Override
    public void defaults() {
        allow();
    }

    @GET
    public void checkIfFixturesServletDisableShields() {
    }

}
