package io.yawp.testing.appengine.models;

import io.yawp.repository.pipes.Pipe;

public class LikeToCounterPipe extends Pipe<Like, Counter> {
    @Override
    public void configureSinks(Like like) {
        addSinkId(like.getCounterId());
    }

    @Override
    public void flux(Like like, Counter counter) {
        counter.inc();
    }

    @Override
    public void reflux(Like like, Counter counter) {
        counter.dec();
    }
}
