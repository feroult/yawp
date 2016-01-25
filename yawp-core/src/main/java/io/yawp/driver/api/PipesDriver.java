package io.yawp.driver.api;

import io.yawp.repository.pipes.Pipe;

public interface PipesDriver {

    void save(Pipe pipe, Object object);
    
}
