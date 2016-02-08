package io.yawp.driver.api;

import io.yawp.repository.pipes.Pipe;

public interface PipesDriver {

    void flux(Pipe pipe, Object object);

    void reflux(Pipe pipe, Object object, StringBuilder sb);

}
