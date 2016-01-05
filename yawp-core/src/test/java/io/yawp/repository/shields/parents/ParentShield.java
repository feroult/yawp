package io.yawp.repository.shields.parents;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.shields.Shield;

public class ParentShield extends Shield<Parent> {

    @Override
    public void defaults() {
        allow();
    }

    @GET
    public void checkIfFixturesServletDisableShields() {
    }

}
