package io.yawp.repository.pipes;

public abstract class PipeFlow {
    public abstract boolean isActive();

    public abstract void flux();

    public abstract void reflux();
}
