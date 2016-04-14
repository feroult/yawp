package io.yawp.repository.shields.hierarchy;

import io.yawp.repository.shields.Shield;

public abstract class AbstractShield<T> extends Shield<T> {

    @Override
    public void defaults() {
        allow();
    }
    
}
