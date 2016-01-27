package io.yawp.repository.pipes;

public class Sink<S> {

    private S sink;

    public Sink(S sink) {
        this.sink = sink;
    }

    public S to(String tag) {
        return sink;
    }

    public SinkTag<S> remember(String tag) {
        return new SinkTag<>(sink, tag);
    }

    public S added() {
        return sink;
    }
}
